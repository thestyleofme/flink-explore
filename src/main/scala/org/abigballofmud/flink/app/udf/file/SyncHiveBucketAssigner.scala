package org.abigballofmud.flink.app.udf.file

import java.time.{Instant, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

import org.abigballofmud.flink.app.model.SyncConfig
import org.apache.flink.core.io.SimpleVersionedSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/06 15:03
 * @since 1.0
 */
class SyncHiveBucketAssigner(syncConfig: SyncConfig) extends BucketAssigner[String, String] {

  override def getBucketId(element: String, context: BucketAssigner.Context): String = {
    // 这里应该使用element里面的字段作为event-time 这里element是按照逗号分割的csv文件，最后一个为event-time(时间戳)
    val splitArr: Array[String] = element.split(",")
    s"${syncConfig.syncHdfs.partitionName}=${
      LocalDateTime.ofInstant(
        Instant.ofEpochMilli(splitArr(splitArr.length - 1).toLong),
        ZoneId.systemDefault()
      ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }"
  }

  override def getSerializer: SimpleVersionedSerializer[String] = {
    SimpleVersionedStringSerializer.INSTANCE
  }
}
