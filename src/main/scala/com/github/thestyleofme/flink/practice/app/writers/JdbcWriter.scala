package com.github.thestyleofme.flink.practice.app.writers

import com.github.thestyleofme.flink.practice.app.constansts.{CommonConstant, DbTypeConstant, KafkaSourceFrom}
import com.github.thestyleofme.flink.practice.app.model.SyncConfig
import com.github.thestyleofme.flink.practice.app.utils.{CommonUtil, OracleSyncJdbcUtil, SyncJdbcUtil}
import com.typesafe.scalalogging.Logger
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala._
import org.apache.flink.types.Row
import org.slf4j.LoggerFactory

import java.util.Objects

/**
 * <p>
 * jdbc实时写入
 * NOTE: 存在sink端数据重复问题，应该使用两阶段提交
 * 但是这个类只是用来实时同步数据的，是幂等操作，所以最后不会有重复数据
 * </p>
 *
 * @author isacc 2020/02/28 15:47
 * @since 1.0
 */
object JdbcWriter {

  private val log = Logger(LoggerFactory.getLogger(JdbcWriter.getClass))

  def doWrite(processedDataStream: DataStream[ObjectNode], syncConfig: SyncConfig): Unit = {
    if (syncConfig.syncJdbc.dbType.equalsIgnoreCase(DbTypeConstant.PHOENIX)) {
      System.setProperty("HADOOP_USER_NAME", "hbase")
    }
    // 注意字段顺序以及类型 注意字段顺序以及类型 注意字段顺序以及类型 !!!!
    if (Objects.nonNull(syncConfig.syncJdbc.upsert)) {
      log.info("use replace into...")
      val replaceDataStream: DataStream[Row] = processedDataStream.getSideOutput(CommonUtil.UPSERT)
        .map(SyncJdbcUtil.canalInsertTransformToRow)
      handleJdbc(replaceDataStream, syncConfig, CommonConstant.UPSERT)
    } else {
      var insertDataStream: DataStream[Row] = null
      var updateDataStream: DataStream[Row] = null
      if (syncConfig.sourceKafka.sourceFrom.equalsIgnoreCase(KafkaSourceFrom.CANAL)) {
        insertDataStream = processedDataStream.getSideOutput(CommonUtil.INSERT)
          .map(SyncJdbcUtil.canalInsertTransformToRow)
        updateDataStream = processedDataStream.getSideOutput(CommonUtil.UPDATE)
          .map(SyncJdbcUtil.canalUpdateTransformToRow)
      } else if (syncConfig.sourceKafka.sourceFrom.equalsIgnoreCase(KafkaSourceFrom.ORACLE_KAFKA_CONNECTOR)) {
        insertDataStream = processedDataStream.getSideOutput(CommonUtil.INSERT)
          .map(OracleSyncJdbcUtil.insertTransformToRow(syncConfig))
        updateDataStream = processedDataStream.getSideOutput(CommonUtil.UPDATE)
          .map(OracleSyncJdbcUtil.updateTransformToRow(syncConfig))
      }
      handleJdbc(insertDataStream, syncConfig, CommonConstant.INSERT)
      handleJdbc(updateDataStream, syncConfig, CommonConstant.UPDATE)
    }
    var deleteDataStream: DataStream[Row] = null
    if (syncConfig.sourceKafka.sourceFrom.equalsIgnoreCase(KafkaSourceFrom.CANAL)) {
      deleteDataStream = processedDataStream.getSideOutput(CommonUtil.DELETE)
        .map(SyncJdbcUtil.canalDeleteTransformToRow)
    } else if (syncConfig.sourceKafka.sourceFrom.equalsIgnoreCase(KafkaSourceFrom.ORACLE_KAFKA_CONNECTOR)) {
      deleteDataStream = processedDataStream.getSideOutput(CommonUtil.DELETE)
        .map(OracleSyncJdbcUtil.deleteTransformToRow(syncConfig))
    }
    handleJdbc(deleteDataStream, syncConfig, CommonConstant.DELETE)
  }


  /**
   * 实时同步
   *
   * @param dataStream    DataStream[Row]
   * @param syncConfig    SyncConfig
   * @param operationType 操作类型
   */
  def handleJdbc(dataStream: DataStream[Row], syncConfig: SyncConfig, operationType: String): Unit = {
    val builder: JDBCOutputFormat.JDBCOutputFormatBuilder = JDBCOutputFormat.buildJDBCOutputFormat()
      .setDrivername(syncConfig.syncJdbc.driver)
      .setDBUrl(syncConfig.syncJdbc.jdbcUrl)
      .setUsername(syncConfig.syncJdbc.user)
      .setPassword(syncConfig.syncJdbc.pwd)
      .setBatchInterval(syncConfig.syncJdbc.batchInterval)
    operationType match {
      case CommonConstant.INSERT => builder
        .setQuery(syncConfig.syncJdbc.insert.query)
        .setSqlTypes(syncConfig.syncJdbc.insert.sqlTypes)
      case CommonConstant.UPSERT => builder
        .setQuery(syncConfig.syncJdbc.upsert.query)
        .setSqlTypes(syncConfig.syncJdbc.upsert.sqlTypes)
      case CommonConstant.UPDATE => builder
        .setQuery(syncConfig.syncJdbc.update.query)
        .setSqlTypes(syncConfig.syncJdbc.update.sqlTypes)
      case CommonConstant.DELETE => builder
        .setQuery(syncConfig.syncJdbc.delete.query)
        .setSqlTypes(syncConfig.syncJdbc.delete.sqlTypes)
    }
    dataStream.writeUsingOutputFormat(builder.finish())
  }

}
