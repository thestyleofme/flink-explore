package org.abigballofmud.flink.app.writers

import java.util.Objects

import com.typesafe.scalalogging.Logger
import org.abigballofmud.flink.app.constansts.CommonConstant
import org.abigballofmud.flink.app.model.SyncConfig
import org.abigballofmud.flink.app.utils.{CommonUtil, SyncJdbcUtil}
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala._
import org.apache.flink.types.Row
import org.slf4j.LoggerFactory

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
    // 注意字段顺序以及类型 注意字段顺序以及类型 注意字段顺序以及类型 !!!!
    if (Objects.nonNull(syncConfig.syncJdbc.replace)) {
      log.info("use replace into...")
      val replaceDataStream: DataStream[Row] = processedDataStream.getSideOutput(CommonUtil.REPLACE).map(SyncJdbcUtil.canalInsertTransformToRow)
      handleJdbc(replaceDataStream, syncConfig, CommonConstant.REPLACE)
    } else {
      val insertDataStream: DataStream[Row] = processedDataStream.getSideOutput(CommonUtil.INSERT).map(SyncJdbcUtil.canalInsertTransformToRow)
      val updateDataStream: DataStream[Row] = processedDataStream.getSideOutput(CommonUtil.UPDATE).map(SyncJdbcUtil.canalUpdateTransformToRow)
      handleJdbc(insertDataStream, syncConfig, CommonConstant.INSERT)
      handleJdbc(updateDataStream, syncConfig, CommonConstant.UPDATE)
    }
    val deleteDataStream: DataStream[Row] = processedDataStream.getSideOutput(CommonUtil.DELETE).map(SyncJdbcUtil.canalDeleteTransformToRow)
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
      case CommonConstant.REPLACE => builder
        .setQuery(syncConfig.syncJdbc.replace.query)
        .setSqlTypes(syncConfig.syncJdbc.replace.sqlTypes)
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
