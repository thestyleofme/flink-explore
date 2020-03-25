package org.abigballofmud.flink.sqlsubmit

import java.util.Properties

import org.abigballofmud.flink.apitest.sinktest.kafka.StringKafkaSerializationSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/10 17:15
 * @since 1.0
 */
//noinspection DuplicatedCode
object SourceGenerator {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val logStream: DataStream[String] = env.readTextFile("src/main/resources/user_behavior.log")
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "hdsp001:6667,hdsp002:6667,hdsp003:6667")
    properties.setProperty("batch.size", "16384")
    logStream.addSink(new FlinkKafkaProducer[String](
      "user_behavior",
      new StringKafkaSerializationSchema("user_behavior", 1),
      properties,
      FlinkKafkaProducer.Semantic.AT_LEAST_ONCE
    ))
    env.execute("log to kafka")
  }

}
