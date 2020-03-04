package org.abigballofmud.flink.app.writers

import com.google.gson.Gson
import org.abigballofmud.flink.apitest.SensorReading
import org.abigballofmud.flink.apitest.sinktest.RedisSinkTest.config
import org.abigballofmud.flink.app.model.SyncConfig
import org.abigballofmud.flink.app.udf.SyncRedisMapper
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/04 19:50
 * @since 1.0
 */
object RedisWriter {

  def genFlinkJedisPoolConfig(syncConfig: SyncConfig): FlinkJedisPoolConfig = {
    new FlinkJedisPoolConfig.Builder()
      .setHost(syncConfig.syncRedis.redisHost)
      .setPort(syncConfig.syncRedis.redisPort)
      .setPassword(syncConfig.syncRedis.redisPassword)
      .build()
  }

  def doWrite(syncConfig: SyncConfig, kafkaStream: DataStream[ObjectNode]): Unit = {
    val flinkJedisPoolConfig: FlinkJedisPoolConfig = genFlinkJedisPoolConfig(syncConfig)
    kafkaStream.addSink(new RedisSink[ObjectNode](flinkJedisPoolConfig, new SyncRedisMapper(syncConfig)))
  }

}
