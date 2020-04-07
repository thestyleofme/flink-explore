package org.abigballofmud.flink.practice.app.model

/**
 * <p>
 * jdbc类型同步信息
 * </p>
 *
 * @param dbType        数据库类型
 * @param driver        驱动
 * @param jdbcUrl       jdbc url
 * @param user          user
 * @param pwd           password
 * @param schema        数据库
 * @param table         写入的表名
 * @param batchInterval 批次大小，可根据源表的数据变化情况动态设置，若变化比较大，批次可设置大一点，变化小，可调小
 * @param cols          非canal时，为了方便处理，指定cols
 * @param pk            非canal时,，为了方便处理，指定pk
 * @param upsert        upsert的sql
 * @param update        update的sql
 * @param insert        insert的sql
 * @param delete        delete的sql
 * @author isacc 2020/03/06 14:38
 * @since 1.0
 */
case class SyncJdbc(dbType: String,
                    driver: String,
                    jdbcUrl: String,
                    user: String,
                    pwd: String,
                    schema: String,
                    table: String,
                    batchInterval: Int = 1,
                    cols: java.util.List[CommonColumn],
                    pk: String,
                    var upsert: QueryAndSqlType,
                    var update: QueryAndSqlType,
                    var insert: QueryAndSqlType,
                    var delete: QueryAndSqlType) extends Serializable

/**
 * 对jdbc操作的封装
 *
 * @param query    具体的sql语句，使用PreparedStatement的格式
 * @param colTypes 占位符的类型
 * @param sqlTypes 占位符的类型对应的int值，java.sql.Types
 *
 */
case class QueryAndSqlType(query: String,
                           colTypes: String,
                           var sqlTypes: Array[Int]
                          ) extends Serializable