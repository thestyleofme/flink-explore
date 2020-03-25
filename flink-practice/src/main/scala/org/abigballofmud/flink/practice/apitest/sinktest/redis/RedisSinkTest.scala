package org.abigballofmud.flink.practice.apitest.sinktest.redis

import org.abigballofmud.flink.practice.apitest.SensorReading
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/03 0:38
 * @since 1.0
 */
//noinspection DuplicatedCode
object RedisSinkTest {

  private val config: FlinkJedisPoolConfig = new FlinkJedisPoolConfig.Builder()
    .setHost("hdsp004")
    .setPort(6379)
    .setPassword("hdsp_dev")
    .build()

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val stream: DataStream[String] = env.readTextFile("src/main/resources/sensor.txt")

    val dataStream: DataStream[SensorReading] = stream.map(data => {
      val splitArr: Array[String] = data.split(",")
      SensorReading(splitArr(0).trim, splitArr(1).trim.toLong, splitArr(2).trim.toDouble)
    })

    dataStream.addSink(new RedisSink[SensorReading](config, new MyRedisMapper()))

    env.execute("redis sink test")
  }

}

class MyRedisMapper() extends RedisMapper[SensorReading] {

  // HSET key field value
  override def getCommandDescription: RedisCommandDescription = {
    new RedisCommandDescription(RedisCommand.HSET, "sensor_temperature")
  }

  override def getKeyFromData(data: SensorReading): String = {
    data.id
  }

  override def getValueFromData(data: SensorReading): String = {
    data.temperature.toString
  }
}
