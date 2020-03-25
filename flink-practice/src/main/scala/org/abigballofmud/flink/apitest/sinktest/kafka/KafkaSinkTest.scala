package org.abigballofmud.flink.apitest.sinktest.kafka

import java.util.Properties

import com.google.gson.Gson
import org.abigballofmud.flink.apitest.SensorReading
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/02 14:47
 * @since 1.0
 */
//noinspection DuplicatedCode
object KafkaSinkTest {

  private val gson = new Gson()

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(8)

    val stream: DataStream[String] = env.readTextFile("src/main/resources/sensor.txt")

    val dataStream: DataStream[String] = stream.map(data => {
      val splitArr: Array[String] = data.split(",")
      val sensorReading: SensorReading = SensorReading(splitArr(0).trim, splitArr(1).trim.toLong, splitArr(2).trim.toDouble)
      gson.toJson(sensorReading)
    })

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "hdsp001:6667,hdsp002:6667,hdsp003:6667")
    dataStream.addSink(new FlinkKafkaProducer[String](
      "flink-kafka-sink-test",
      new StringKafkaSerializationSchema("flink-kafka-sink-test", 1),
      properties,
      FlinkKafkaProducer.Semantic.EXACTLY_ONCE,
      3
    ))

    //    dataStream.print()

    env.execute("kafka sink test")
  }
}
