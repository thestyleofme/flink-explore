package com.github.thestyleofme.flink.practice.app.udf.file

import com.github.thestyleofme.flink.practice.app.model.SyncConfig
import org.apache.flink.core.io.SimpleVersionedSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/06 15:03
 * @since 1.0
 */
class SyncHiveTextBucketAssigner(syncConfig: SyncConfig) extends BucketAssigner[String, String] {

  override def getBucketId(element: String, context: BucketAssigner.Context): String = {
    // 这里应该使用element里面的字段作为event-time 这里element是按照hive分割符分割的csv文件，最后一个为event-time(时间戳)
    val splitArr: Array[String] = element.split(syncConfig.syncHive.fieldsDelimited)
    // 是否分区
    if (syncConfig.syncHive.isPartition) {
      s"${syncConfig.syncHive.partitionName}=${
        LocalDateTime.ofInstant(
          Instant.ofEpochMilli(splitArr(splitArr.length - 1).toLong),
          ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
      }"
    } else {
      s""
    }
  }

  override def getSerializer: SimpleVersionedSerializer[String] = {
    SimpleVersionedStringSerializer.INSTANCE
  }
}
