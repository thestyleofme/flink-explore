package org.abigballofmud.flink.app.model

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/26 16:55
 * @since 1.0
 */
case class SyncConfig(syncFlink: SyncFlink,
                     syncKafka: SyncKafka,
                     syncJdbc: SyncJdbc
                    ) extends Serializable

/**
 * flink执行配置类
 *
 * @param jobName flink的jobName
 */
case class SyncFlink(jobName: String,
                     sourceSchema: String,
                     sourceTable: String) extends Serializable

/**
 * kafka信息
 *
 * @param kafkaBootstrapServers kafkaBootstrapServers
 * @param kafkaTopic            topic
 * @param initDefaultOffset     初始offset
 */
case class SyncKafka(kafkaBootstrapServers: String,
                     kafkaTopic: String,
                     initDefaultOffset: String) extends Serializable

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