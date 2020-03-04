package org.abigballofmud.flink.app.model

import redis.clients.jedis.Protocol

/**
 * <p>
 * 实时数仓flink执行配置case类
 * </p>
 *
 * @author isacc 2020/02/26 16:55
 * @since 1.0
 */
case class SyncConfig(syncFlink: SyncFlink,
                      sourceKafka: SourceKafka,
                      syncJdbc: SyncJdbc,
                      syncKafka: SyncKafka,
                      syncEs6: SyncEs6,
                      syncRedis: SyncRedis
                     ) extends Serializable

/**
 * flink执行配置类
 *
 * @param jobName flink的jobName
 */
case class SyncFlink(jobName: String,
                     sourceSchema: String,
                     sourceTable: String,
                     checkPointPath: String,
                     writeType: String) extends Serializable

/**
 * kafka信息
 *
 * @param kafkaBootstrapServers kafkaBootstrapServers
 * @param kafkaTopic            topic
 * @param initDefaultOffset     初始offset
 */
case class SourceKafka(kafkaBootstrapServers: String,
                       kafkaTopic: String,
                       initDefaultOffset: String) extends Serializable

/**
 * 写入kafka的信息
 *
 * @param kafkaBootstrapServers kafkaBootstrapServers
 * @param kafkaTopic            topic
 */
case class SyncKafka(kafkaBootstrapServers: String,
                     kafkaTopic: String) extends Serializable

/**
 * 写入redis
 *
 * @param redisHost     redisHost
 * @param redisPort     redisPort
 * @param redisPassword redisPassword
 * @param redisDataBase redisDataBase
 */
case class SyncRedis(redisHost: String,
                     redisPort: Int,
                     redisPassword: String,
                     redisDataBase: Int = Protocol.DEFAULT_DATABASE,
                     redisKey: String) extends Serializable

/**
 * 同步到es6的配置信息
 *
 * @param httpHosts           es集群，如: hdsp001:9200,hdsp002:9200,hdsp003:9200
 * @param esSchema            es schema,如 http,https
 * @param esIndex             es index
 * @param esType              es type
 * @param bulkFlushMaxActions 批次大小
 */
case class SyncEs6(httpHosts: String,
                   esSchema: String = "http",
                   esIndex: String,
                   esType: String,
                   bulkFlushMaxActions: Int = 1) extends Serializable

/**
 * jdbc类型同步信息
 *
 * @param dbType        数据库类型
 * @param driver        驱动
 * @param jdbcUrl       jdbc url
 * @param user          user
 * @param pwd           password
 * @param schema        数据库
 * @param table         写入的表名
 * @param batchInterval 批次大小，可根据源表的数据变化情况动态设置，若变化比较大，批次可设置大一点，变化小，可调小
 * @param update        update的sql
 * @param insert        insert的sql
 * @param delete        delete的sql
 */
case class SyncJdbc(dbType: String,
                    driver: String,
                    jdbcUrl: String,
                    user: String,
                    pwd: String,
                    schema: String,
                    table: String,
                    batchInterval: Int = 1,
                    var replace: QueryAndSqlType,
                    var update: QueryAndSqlType,
                    var insert: QueryAndSqlType,
                    var delete: QueryAndSqlType) extends Serializable

/**
 *
 * @param query    具体的sql语句，使用PreparedStatement的格式
 * @param colTypes 占位符的类型
 * @param sqlTypes 占位符的类型对应的int值，java.sql.Types
 */
case class QueryAndSqlType(query: String,
                           colTypes: String,
                           var sqlTypes: Array[Int]
                          ) extends Serializable