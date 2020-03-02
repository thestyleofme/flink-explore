package org.abigballofmud.flink.app.utils

import java.lang.reflect.Type
import java.util
import java.util.Objects
import java.util.concurrent.TimeUnit

import com.google.gson.reflect.TypeToken
import com.typesafe.scalalogging.Logger
import org.abigballofmud.flink.app.SyncApp.gson
import org.abigballofmud.flink.app.constansts.CommonConstant
import org.abigballofmud.flink.app.model.SyncConfig
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.time.Time
import org.apache.flink.api.java.io.TextInputFormat
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.core.fs.Path
import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory

/**
 * <p>
 * flink常用方法工具类
 * </p>
 *
 * @author isacc 2020/02/28 11:39
 * @since 1.0
 */
object CommonUtil {

  private val log = Logger(LoggerFactory.getLogger(CommonUtil.getClass))

  val UPDATE: OutputTag[ObjectNode] = new OutputTag[ObjectNode](CommonConstant.UPDATE)
  val INSERT: OutputTag[ObjectNode] = new OutputTag[ObjectNode](CommonConstant.INSERT)
  val REPLACE: OutputTag[ObjectNode] = new OutputTag[ObjectNode](CommonConstant.REPLACE)
  val DELETE: OutputTag[ObjectNode] = new OutputTag[ObjectNode](CommonConstant.DELETE)

  /**
   * 设置初始kafka的offset
   *
   * @param kafkaConsumer FlinkKafkaConsumer[ObjectNode]
   * @param syncConfig    SyncConfig
   */
  def initOffset(kafkaConsumer: FlinkKafkaConsumer[ObjectNode], syncConfig: SyncConfig): Unit = {
    if (syncConfig.sourceKafka.initDefaultOffset.equalsIgnoreCase(CommonConstant.KAFKA_INIT_OFFSET_EARLIEST)) {
      kafkaConsumer.setStartFromEarliest()
    } else if (syncConfig.sourceKafka.initDefaultOffset.equalsIgnoreCase(CommonConstant.KAFKA_INIT_OFFSET_LATEST)) {
      kafkaConsumer.setStartFromLatest()
    } else {
      // 指定了分区以及偏移量 传入的json格式：{"0":100,"1":200,"3":300}
      val specificStartOffsets = new java.util.HashMap[KafkaTopicPartition, java.lang.Long]()
      val typeToken: Type = new TypeToken[java.util.Map[String, java.lang.Long]]() {}.getType
      val map: java.util.Map[String, java.lang.Long] = gson.fromJson(syncConfig.sourceKafka.initDefaultOffset, typeToken)
      val iterator: util.Iterator[util.Map.Entry[String, java.lang.Long]] = map.entrySet().iterator()
      while (iterator.hasNext) {
        val entry: util.Map.Entry[String, java.lang.Long] = iterator.next()
        specificStartOffsets.put(new KafkaTopicPartition(syncConfig.sourceKafka.kafkaTopic, entry.getKey.toInt), entry.getValue)
      }
      kafkaConsumer.setStartFromSpecificOffsets(specificStartOffsets)
    }
  }

  /**
   * 读取配置文件转为SyncConfig
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
    gson.fromJson(configStr, classOf[SyncConfig])
  }

  /**
   * flink容错机制设置 如checkpoint、重启策略等
   *
   * @param env        StreamExecutionEnvironment
   * @param syncConfig SyncConfig
   */
  def toleranceOption(env: StreamExecutionEnvironment, syncConfig: SyncConfig): Unit = {
    // checkpoint设置
    val checkpointConfig: CheckpointConfig = env.getCheckpointConfig
    checkpointConfig.setCheckpointInterval(5000L)
    checkpointConfig.setTolerableCheckpointFailureNumber(1)
    checkpointConfig.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    checkpointConfig.setMaxConcurrentCheckpoints(1)
    checkpointConfig.setPreferCheckpointForRecovery(true)
    // 设置重启策略
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.of(60, TimeUnit.SECONDS)))
    // 持久化checkpoint
    val fsStateBackend: StateBackend = new FsStateBackend(syncConfig.syncFlink.checkPointPath)
    log.info("checkpoint and restart strategy all configured success")
    env.setStateBackend(fsStateBackend)
  }

  /**
   * 对kafka数据分流
   *
   * @param kafkaStream DataStream[ObjectNode]
   * @param syncConfig  SyncConfig
   * @return DataStream[ObjectNode]
   */
  def splitDataStream(kafkaStream: DataStream[ObjectNode], syncConfig: SyncConfig): DataStream[ObjectNode] = {
    kafkaStream.process(new ProcessFunction[ObjectNode, ObjectNode] {
      override def processElement(value: ObjectNode, ctx: ProcessFunction[ObjectNode, ObjectNode]#Context, out: Collector[ObjectNode]): Unit = {
        if (Objects.nonNull(syncConfig.syncJdbc.replace)) {
          // 配置了replace 两批
          if (value.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.UPDATE) ||
            value.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.INSERT)) {
            ctx.output(REPLACE, value)
          }
        } else {
          // 未配置replace 三批
          if (value.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.UPDATE)) {
            ctx.output(UPDATE, value)
          } else if (value.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.INSERT)) {
            ctx.output(INSERT, value)
          }
        }
        if (value.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.DELETE)) {
          ctx.output(DELETE, value)
        } else {
          // 其他
          out.collect(value)
        }
      }
    })
  }

}
