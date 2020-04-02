package org.abigballofmud.flink.practice.hive

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala.StreamTableEnvironment
import org.apache.flink.table.api.{EnvironmentSettings, Table}

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/11 15:49
 * @since 1.0
 */
object FlinkSqlHiveScala {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val settings: EnvironmentSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
    val tableEnv: StreamTableEnvironment = StreamTableEnvironment.create(env, settings)
    //    val tableEnv: TableEnvironment = TableEnvironment.create(settings)
    //    val name = "hive"
    //    val defaultDatabase = "test"
    //    val hiveConfDir = "src/main/resources"
    //    val version = "3.1.0.3.1.0.0-78"
    //    val hive: HiveCatalog = new HiveCatalog(name, defaultDatabase, hiveConfDir, version)
    //    tableEnv.registerCatalog("hive", hive)
    //
    //    // set the HiveCatalog as the current catalog of the session
    //    tableEnv.useCatalog("hive")
    //    // 当结果表是 Hive 表时，可以使用 Overwrite 语法，也可以使用静态 Partition 的语法，这需要打开 Hive 的方言：
    //    tableEnv.getConfig.setSqlDialect(SqlDialect.HIVE)
    //    val table: Table = tableEnv.sqlQuery("select * from dev_test_demo0210")
    //    tableEnv.toRetractStream[Row](table).print()

    //    val properties = new Properties()
    //    properties.setProperty("bootstrap.servers", "hdsp001:6667,hdsp002:6667,hdsp003:6667")
    //    val kafkaConsumer: FlinkKafkaConsumer[ObjectNode] = new FlinkKafkaConsumer(
    //      "user_behavior",
    //      new JSONKeyValueDeserializationSchema(true),
    //      properties)
    //    kafkaConsumer.setStartFromEarliest()
    //    val ds: DataStream[(String, String, String, String, Timestamp)] = env.addSource(kafkaConsumer).map(objectNode =>
    //      (
    //        objectNode.get("value").get("user_id").asText(),
    //        objectNode.get("value").get("item_id").asText(),
    //        objectNode.get("value").get("category_id").asText(),
    //        objectNode.get("value").get("behavior").asText(),
    //        Timestamp.valueOf(
    //          ZonedDateTime.parse(objectNode.get("value").get("ts").asText(),
    //            DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault()))
    //            .toLocalDateTime
    //        )
    //      )
    //    )

    //    val table: Table = tableEnv.fromDataStream(ds, 'user_id, 'item_id, 'category_id, 'behavior, 'ts)
    //    val table: Table = ds.toTable(tableEnv, 'user_id, 'item_id, 'category_id, 'behavior, 'ts)
    //    tableEnv.sqlQuery(
    //      s"SELECT * FROM $table"
    //    ).toAppendStream[(String, String, String, String, Timestamp)].print()
    //    tableEnv
    //      .connect(
    //        new Kafka()
    //          .version("universal")
    //          .topic("user_behavior")
    //          .property("zookeeper.connect", "hdsp001:2181,hdsp002:2181,hdsp003:2181")
    //          .property("bootstrap.servers", "hdsp001:6667,hdsp002:6667,hdsp003:6667")
    //          .startFromEarliest()
    //      )
    //      .withFormat(new Json())
    //      .withSchema(new Schema()
    //        .field("ts", DataTypes.TIMESTAMP(3))
    //        .rowtime(new Rowtime()
    //          .timestampsFromField("ts")
    //          .watermarksPeriodicBounded(60000)
    //        )
    //        .field("user_id", DataTypes.STRING())
    //        .field("item_id", DataTypes.STRING())
    //        .field("category_id", DataTypes.STRING())
    //        .field("behavior", DataTypes.STRING())
    //      )
    //      .inAppendMode()
    //      .createTemporaryTable("kafkaTable")

    tableEnv.sqlUpdate(
      s"""
         |CREATE TABLE user_log (
         |    user_id VARCHAR,
         |    item_id VARCHAR,
         |    category_id VARCHAR,
         |    behavior VARCHAR,
         |    ts TIMESTAMP(3)
         |) WITH (
         |    'connector.type' = 'kafka',
         |    'connector.version' = 'universal',
         |    'connector.topic' = 'user_behavior',
         |    'connector.startup-mode' = 'earliest-offset',
         |    'connector.properties.zookeeper.connect' = 'hdsp001:2181,hdsp002:2181,hdsp003:2181',
         |    'connector.properties.bootstrap.servers' = 'hdsp001:6667,hdsp002:6667,hdsp003:6667',
         |    'update-mode' = 'append',
         |    'format.type' = 'json',
         |    'format.derive-schema' = 'true'
         |)
         |""".stripMargin)

    tableEnv.sqlUpdate(
      s"""
         |CREATE TABLE pvuv_sink (
         |    dt VARCHAR,
         |    pv BIGINT,
         |    uv BIGINT
         |) WITH (
         |    'connector.type' = 'jdbc',
         |    'connector.url' = 'jdbc:mysql://dev.hdsp.hand.com:7233/hdsp_test',
         |    'connector.table' = 'pvuv_sink',
         |    'connector.username' = 'hdsp_dev',
         |    'connector.password' = 'hdsp_dev',
         |    'connector.write.flush.max-rows' = '1'
         |)
         |""".stripMargin)

    val sourceTable: Table = tableEnv.sqlQuery(
      s"""
         |SELECT
         |  CAST(DATE_FORMAT(ts, 'yyyy-MM-dd HH:00') AS STRING) AS dt,
         |  COUNT(*) AS pv,
         |  COUNT(DISTINCT user_id) AS uv
         |FROM user_log
         |GROUP BY CAST(DATE_FORMAT(ts, 'yyyy-MM-dd HH:00') AS STRING)
         |""".stripMargin)

    //    tableEnv.toRetractStream[Row](sourceTable).print()

    tableEnv.sqlUpdate(
      s"""
         |INSERT INTO pvuv_sink
         |SELECT
         |  *
         |FROM $sourceTable
         |""".stripMargin)


    tableEnv.execute("flink sql hive")
  }

}
