package org.abigballofmud.flink.practice.apitest.sinktest.file

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

import org.apache.flink.api.common.serialization.{SimpleStringEncoder, SimpleStringSchema}
import org.apache.flink.core.fs.Path
import org.apache.flink.core.io.SimpleVersionedSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy
import org.apache.flink.streaming.api.functions.sink.filesystem.{BucketAssigner, OutputFileConfig, StreamingFileSink}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaConsumerBase}

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/06 9:26
 * @since 1.0
 */
//noinspection DuplicatedCode
object StreamingFileSinkTest {

  def main(args: Array[String]): Unit = {

    // 由于需要写hdfs有权限问题 这里造假当前用户
    System.setProperty("HADOOP_USER_NAME", "hdfs")
    System.setProperty("user.name", "hdfs")

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.enableCheckpointing(5000L)

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "hdsp001:6667,hdsp002:6667,hdsp003:6667")
    val kafkaConsumer: FlinkKafkaConsumerBase[String] = new FlinkKafkaConsumer[String](
      "example",
      new SimpleStringSchema(),
      properties)
    val specificStartOffsets = new java.util.HashMap[KafkaTopicPartition, java.lang.Long]()
    specificStartOffsets.put(new KafkaTopicPartition("example", 0), 6500L)
    kafkaConsumer.setStartFromSpecificOffsets(specificStartOffsets)
    val kafkaStream: DataStream[String] = env.addSource(kafkaConsumer)

    //    val sink: StreamingFileSink[String] = StreamingFileSink
    //      .forRowFormat(new Path("hdfs://hdsp001:8020/data/flink-test"), new SimpleStringEncoder[String]("UTF-8"))
    //      .withBucketAssigner(new MyBucketAssigner)
    //      //      .withRollingPolicy(
    //      //        DefaultRollingPolicy.builder()
    //      //          .withRolloverInterval(TimeUnit.MINUTES.toMillis(5))
    //      //          .withInactivityInterval(TimeUnit.MINUTES.toMillis(1))
    //      //          .withMaxPartSize(1024 * 1024 * 128)
    //      //          .build()
    //      //            )
    //      .build()

    val config: OutputFileConfig = OutputFileConfig
      .builder()
      .withPartPrefix("part")
      .withPartSuffix(".csv")
      .build()

    val builder: StreamingFileSink.RowFormatBuilder[String, String, _ <: StreamingFileSink.RowFormatBuilder[String, String, _]] =
      StreamingFileSink
        .forRowFormat(new Path("hdfs://hdsp001:8020/data/flink-test"), new SimpleStringEncoder[String]("UTF-8"))
    builder.withBucketAssigner(new MyBucketAssigner)
    builder.withRollingPolicy(OnCheckpointRollingPolicy.build())
    builder.withOutputFileConfig(config)

    kafkaStream.addSink(builder.build())

    env.execute()

  }
}

class MyBucketAssigner extends BucketAssigner[String, String] {

  override def getBucketId(element: String, context: BucketAssigner.Context): String = {
    // 这里应该使用element里面的字段作为event-time的 我这里模仿下
    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  }

  override def getSerializer: SimpleVersionedSerializer[String] = {
    SimpleVersionedStringSerializer.INSTANCE
  }

}
