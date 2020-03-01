package org.abigballofmud.flink.app

import java.util.Properties

import com.google.gson.Gson
import com.typesafe.scalalogging.Logger
import org.abigballofmud.flink.app.constansts.CommonConstant
import org.abigballofmud.flink.app.model.SyncConfig
import org.abigballofmud.flink.app.udf.SchemaAndTableFilter
import org.abigballofmud.flink.app.utils.{CommonUtil, SyncJdbcUtil}
import org.abigballofmud.flink.app.writers.JdbcWriter
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.{DataStream, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import org.slf4j.LoggerFactory

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

  private val log = Logger(LoggerFactory.getLogger(SyncApp.getClass))
  val gson: Gson = new Gson()

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // 获取flink执行配置
    val syncConfig: SyncConfig = CommonUtil.genSyncConfig(args)
    log.info("flink config file load success")
    // 根据字段类型生成sqlType
    SyncJdbcUtil.genSqlTypes(syncConfig)
    // flink容错机制设置 如checkpoint、重启策略等
    CommonUtil.toleranceOption(env, syncConfig)
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", syncConfig.syncKafka.kafkaBootstrapServers)
    val kafkaConsumer: FlinkKafkaConsumer[ObjectNode] = new FlinkKafkaConsumer(
      syncConfig.syncKafka.kafkaTopic,
      new JSONKeyValueDeserializationSchema(true),
      properties)
    log.info("starting read kafka...")
    // 设置初始kafka读取的offset
    CommonUtil.initOffset(kafkaConsumer, syncConfig)
    val kafkaStream: DataStream[ObjectNode] = env.addSource(kafkaConsumer)
      .filter(new SchemaAndTableFilter(syncConfig))
    // 分流，若配置了replace就分为两批，没有则三批
    val processedDataStream: DataStream[ObjectNode] = CommonUtil.splitDataStream(kafkaStream, syncConfig)
    // sink
    JdbcWriter.doWrite(processedDataStream, syncConfig)
    log.info("flink starting...")
    env.execute(syncConfig.syncFlink.jobName)
  }
}