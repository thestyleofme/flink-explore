package org.abigballofmud.flink.practice.app.model

import redis.clients.jedis.Protocol

/**
 *
 * <p>
 * 写入redis
 * </p>
 *
 * @param redisHost     redisHost
 * @param redisPort     redisPort
 * @param redisPassword redisPassword
 * @param redisDataBase redisDataBase
 * @author isacc 2020/03/06 14:37
 * @since 1.0
 */
case class SyncRedis(redisHost: String,
                     redisPort: Int,
                     redisPassword: String,
                     redisDataBase: Int = Protocol.DEFAULT_DATABASE,
                     redisKey: String) extends Serializable
