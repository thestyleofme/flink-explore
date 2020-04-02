package org.abigballofmud.flink.practice.app.udf.redis

import java.util
import java.util.Objects

import com.google.gson.Gson
import org.abigballofmud.flink.practice.app.model.SyncConfig
import org.abigballofmud.flink.practice.app.utils.SyncJdbcUtil
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/04 19:56
 * @since 1.0
 */
class SyncRedisMapper(syncConfig: SyncConfig) extends RedisMapper[ObjectNode] {

  override def getCommandDescription: RedisCommandDescription = {
    var redisKey: String = syncConfig.syncRedis.redisKey
    // 默认redis key为topic名称
    if (Objects.isNull(redisKey)) {
      redisKey = syncConfig.sourceKafka.kafkaTopic
    }
    new RedisCommandDescription(RedisCommand.HSET, redisKey)
  }

  override def getKeyFromData(data: ObjectNode): String = {
    val splitArr: Array[String] = data.get("value").findValue("pkNames").get(0).asText().split(",")
    val map: util.HashMap[String, String] = SyncJdbcUtil.genValueMap(data)
    map.get(splitArr(0))
  }

  override def getValueFromData(data: ObjectNode): String = {
    val map: util.HashMap[String, Object] = SyncJdbcUtil.genRealValueMap(data)
    new Gson().toJson(map)
  }
}
