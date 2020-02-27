package org.abigballofmud.flink.app

import java.util.{Objects, Properties}

import com.google.gson.Gson
import org.abigballofmud.flink.app.constansts.CommonConstant
import org.abigballofmud.flink.app.model.SyncConfig
import org.abigballofmud.flink.app.utils.CommonUtil
import org.apache.flink.api.java.io.TextInputFormat
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.core.fs.Path
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.{DataStream, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import org.apache.flink.types.Row
import org.apache.flink.util.Collector

/**
 * <p>
 * 基于canal的数据实时同步
 * --configFilePath src/main/resources/canal-sync-demo.json
 * </p>
 *
 * @author isacc 2020/02/25 14:55
 * @since 1.0
 */
object SyncApp {

  private val gson: Gson = new Gson()

  private val UPDATE: OutputTag[ObjectNode] = new OutputTag[ObjectNode](CommonConstant.UPDATE)
  private val INSERT: OutputTag[ObjectNode] = new OutputTag[ObjectNode](CommonConstant.INSERT)
  private val DELETE: OutputTag[ObjectNode] = new OutputTag[ObjectNode](CommonConstant.DELETE)

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //    env.enableCheckpointing(5000)
    // 获取配置并根据字段类型生成sqlType
    val syncConfig: SyncConfig = genSyncConfig(args)
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", syncConfig.syncKafka.kafkaBootstrapServers)
    val kafkaConsumer: FlinkKafkaConsumer[ObjectNode] = new FlinkKafkaConsumer(
      syncConfig.syncKafka.kafkaTopic,
      new JSONKeyValueDeserializationSchema(true),
      properties)
    // 默认从 latest 开始消费
    kafkaConsumer.setStartFromLatest()
    //    val specificStartOffsets = new java.util.HashMap[KafkaTopicPartition, java.lang.Long]()
    //    specificStartOffsets.put(new KafkaTopicPartition("example", 0), 100L)
    //    kafkaConsumer.setStartFromSpecificOffsets(specificStartOffsets)
    val kafkaStream: DataStream[ObjectNode] = env.addSource(kafkaConsumer)
      .filter(objectNode => objectNode.get("value").get("database").asText().equalsIgnoreCase(syncConfig.syncFlink.sourceSchema))
      .filter(objectNode => objectNode.get("value").get("table").asText().equalsIgnoreCase(syncConfig.syncFlink.sourceTable))
    // 分流
    val processedDataStream: DataStream[ObjectNode] = kafkaStream.process(new ProcessFunction[ObjectNode, ObjectNode] {
      override def processElement(value: ObjectNode, ctx: ProcessFunction[ObjectNode, ObjectNode]#Context, out: Collector[ObjectNode]): Unit = {
        if (value.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.UPDATE)) {
          ctx.output(UPDATE, value)
        } else if (value.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.INSERT)) {
          ctx.output(INSERT, value)
        } else if (value.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.DELETE)) {
          ctx.output(DELETE, value)
        } else {
          // 其他
          out.collect(value)
        }
      }
    })
    // 注意字段顺序以及类型
    val insertDataStream: DataStream[Row] = processedDataStream.getSideOutput(INSERT).map(CommonUtil.canalInsertTransformToRow)
    val updateDataStream: DataStream[Row] = processedDataStream.getSideOutput(UPDATE).map(CommonUtil.canalUpdateTransformToRow)
    val deleteDataStream: DataStream[Row] = processedDataStream.getSideOutput(DELETE).map(CommonUtil.canalDeleteTransformToRow)
    handleJdbc(insertDataStream, syncConfig, CommonConstant.INSERT)
    handleJdbc(updateDataStream, syncConfig, CommonConstant.UPDATE)
    handleJdbc(deleteDataStream, syncConfig, CommonConstant.DELETE)

    env.execute(syncConfig.syncFlink.jobName)
  }

  /**
   * 读取配置文件转为SyncConfig并根据字段类型生成sqlTypes
   *
   * @param args Array[String]
   * @return SyncConfig
   */
  def genSyncConfig(args: Array[String]): SyncConfig = {
    val tempEnv: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    tempEnv.setParallelism(1)
    val parameterTool: ParameterTool = ParameterTool.fromArgs(args)
    val configDs: DataSet[String] = tempEnv.readFile(new TextInputFormat(new Path()),
      Objects.requireNonNull(parameterTool.get("configFilePath"),
        "找不到flink执行所需的配置文件，请使用 --configFilePath /path/to/file")
    )
    val configStr: String = configDs.map(_.trim).collect().mkString
    val syncConfig: SyncConfig = gson.fromJson(configStr, classOf[SyncConfig])
    // 生成需要sqlTypes
    CommonUtil.genSqlTypes(syncConfig)
    syncConfig
  }

  /**
   * 实时同步到mysql
   *
   * @param dataStream    DataStream[Row]
   * @param syncConfig    SyncConfig
   * @param operationType 操作类型
   */
  def handleJdbc(dataStream: DataStream[Row], syncConfig: SyncConfig, operationType: String): Unit = {
    val builder: JDBCOutputFormat.JDBCOutputFormatBuilder = JDBCOutputFormat.buildJDBCOutputFormat()
      .setDrivername(syncConfig.syncJdbc.driver)
      .setDBUrl(syncConfig.syncJdbc.jdbcUrl)
      .setUsername(syncConfig.syncJdbc.user)
      .setPassword(syncConfig.syncJdbc.pwd)
      .setBatchInterval(syncConfig.syncJdbc.batchInterval)
    operationType match {
      case CommonConstant.INSERT => builder
        .setQuery(syncConfig.syncJdbc.insert.query)
        .setSqlTypes(syncConfig.syncJdbc.insert.sqlTypes)
      case CommonConstant.UPDATE => builder
        .setQuery(syncConfig.syncJdbc.update.query)
        .setSqlTypes(syncConfig.syncJdbc.update.sqlTypes)
      case CommonConstant.DELETE => builder
        .setQuery(syncConfig.syncJdbc.delete.query)
        .setSqlTypes(syncConfig.syncJdbc.delete.sqlTypes)
    }
    dataStream.writeUsingOutputFormat(builder.finish())
  }

}
