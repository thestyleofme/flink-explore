package org.abigballofmud.flink.practice.app.writers

import java.util

import com.typesafe.scalalogging.Logger
import org.abigballofmud.flink.practice.app.constansts.HiveFileTypeConstant
import org.abigballofmud.flink.practice.app.model.SyncConfig
import org.abigballofmud.flink.practice.app.udf.file.SyncHiveTextBucketAssigner
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.core.fs.Path
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy
import org.apache.flink.streaming.api.functions.sink.filesystem.{OutputFileConfig, StreamingFileSink}
import org.apache.flink.streaming.api.scala._
import org.slf4j.LoggerFactory

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/04 9:51
 * @since 1.0
 */
//noinspection DuplicatedCode
object HiveWriter {

  private val log = Logger(LoggerFactory.getLogger(Es6Writer.getClass))

  def doWrite(syncConfig: SyncConfig, kafkaStream: DataStream[ObjectNode]): Unit = {
    log.info("start writing to hive...")
    syncConfig.syncHive.fileType.toUpperCase match {
      case HiveFileTypeConstant.TEXT =>
        val dataStream: DataStream[String] = kafkaStream.map(objectNode2CsvString(syncConfig))
        dataStream.addSink(genRowFormatBuilder(syncConfig).build())
      case _ =>
        throw new IllegalArgumentException("unsupported hive fileType")
    }
  }

  /**
   *
   * hive text表，将canal中的data转为csv，按照fieldsDelimited进行分割
   *
   * @return MapFunction[ObjectNode, String]
   */
  def objectNode2CsvString(syncConfig: SyncConfig): MapFunction[ObjectNode, String] = {
    new MapFunction[ObjectNode, String]() {
      override def map(objectNode: ObjectNode): String = {
        val data: util.Iterator[util.Map.Entry[String, databind.JsonNode]] = objectNode.get("value").findValue("data").get(0).fields()
        var list: List[String] = List()
        while (data.hasNext) {
          val value: util.Map.Entry[String, databind.JsonNode] = data.next()
          list = list :+ value.getValue.asText()
        }
        // list中增加操作类型 操作时间（event-time）
        list = list :+ objectNode.get("value").get("type").asText()
        list = list :+ objectNode.get("value").get("ts").asText()
        list.mkString(syncConfig.syncHive.fieldsDelimited)
      }
    }
  }

  /**
   * 构造RowFormatBuilder
   *
   * @param syncConfig SyncConfig
   * @return
   */
  def genRowFormatBuilder(syncConfig: SyncConfig): StreamingFileSink.RowFormatBuilder[String, String, _ <: StreamingFileSink.RowFormatBuilder[String, String, _]] = {
    val builder: StreamingFileSink.RowFormatBuilder[String, String, _ <: StreamingFileSink.RowFormatBuilder[String, String, _]] =
      StreamingFileSink
        .forRowFormat(new Path(syncConfig.syncHive.hdfsPath), new SimpleStringEncoder[String]("UTF-8"))
    builder.withBucketAssigner(new SyncHiveTextBucketAssigner(syncConfig))
    builder.withRollingPolicy(OnCheckpointRollingPolicy.build())
    builder.withOutputFileConfig(genOutputFileConfig(syncConfig))
    builder
  }

  /**
   * hdfs文件输出配置
   *
   * @param syncConfig SyncConfig
   * @return
   */
  def genOutputFileConfig(syncConfig: SyncConfig): OutputFileConfig = {
    OutputFileConfig
      .builder()
      .withPartPrefix(syncConfig.syncHive.partPrefix)
      .withPartSuffix(syncConfig.syncHive.partSuffix)
      .build()
  }


}
