package org.abigballofmud.flink.practice.app.writers

import org.abigballofmud.flink.practice.app.model.SyncConfig
import org.abigballofmud.flink.practice.app.udf.redis.SyncRedisMapper
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig

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
