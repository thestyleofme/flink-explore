package org.abigballofmud.flink.app.utils

import java.time.format.DateTimeFormatter
import java.util

import org.abigballofmud.flink.app.constansts.ColTypeConstant
import org.abigballofmud.flink.app.model.{CommonColumn, SyncConfig}
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.types.Row

import scala.collection.mutable

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/18 15:55
 * @since 1.0
 */
//noinspection DuplicatedCode
object OracleSyncJdbcUtil {

  def updateTransformToRow(syncConfig: SyncConfig): MapFunction[ObjectNode, Row] = {
    new MapFunction[ObjectNode, Row]() {
      override def map(objectNode: ObjectNode): Row = {
        // 获取data
        val dataMap: mutable.Map[String, String] = genDataMap(objectNode, "data")
        val row = new Row(dataMap.size)
        var i: Int = 0
        var iterator: util.Iterator[CommonColumn] = syncConfig.syncJdbc.cols.iterator()
        while (iterator.hasNext) {
          val column: CommonColumn = iterator.next()
          // 先封装set字段
          if (!column.colName.equals(syncConfig.syncJdbc.pk)) {
            rowSetField(row, column, i, dataMap)
            i += 1
          }
        }
        iterator = syncConfig.syncJdbc.cols.iterator()
        while (iterator.hasNext) {
          val column: CommonColumn = iterator.next()
          // 最后封装pk
          if (column.colName.equals(syncConfig.syncJdbc.pk)) {
            rowSetField(row, column, i, dataMap)
          }
        }
        row
      }
    }
  }

  def insertTransformToRow(syncConfig: SyncConfig): MapFunction[ObjectNode, Row] = {
    new MapFunction[ObjectNode, Row]() {
      override def map(objectNode: ObjectNode): Row = {
        // 获取data
        val dataMap: mutable.Map[String, String] = genDataMap(objectNode, "data")
        val row = new Row(dataMap.size)
        var i: Int = 0
        val iterator: util.Iterator[CommonColumn] = syncConfig.syncJdbc.cols.iterator()
        while (iterator.hasNext) {
          val column: CommonColumn = iterator.next()
          rowSetField(row, column, i, dataMap)
          i += 1
        }
        row
      }
    }
  }

  def deleteTransformToRow(syncConfig: SyncConfig): MapFunction[ObjectNode, Row] = {
    new MapFunction[ObjectNode, Row]() {
      override def map(objectNode: ObjectNode): Row = {
        // 获取data
        val dataMap: mutable.Map[String, String] = genDataMap(objectNode, "before")
        val row = new Row(1)
        val iterator: util.Iterator[CommonColumn] = syncConfig.syncJdbc.cols.iterator()
        while (iterator.hasNext) {
          val column: CommonColumn = iterator.next()
          if (column.colName.equals(syncConfig.syncJdbc.pk)) {
            rowSetField(row, column, 0, dataMap)
          }
        }
        row
      }
    }
  }

  def rowSetField(row: Row, column: CommonColumn, i: Int, dataMap: mutable.Map[String, String]): Unit = {
    if (column.colType.equals(ColTypeConstant.DATE)) {
      row.setField(i, DateUtil.timestamp2LocalDateTime(java.lang.Long.valueOf(dataMap(column.colName)))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
    } else if (column.colType.equals(ColTypeConstant.NUMBER)) {
      row.setField(i, new java.math.BigDecimal(dataMap(column.colName)).longValue())
    } else if (column.colType.equals(ColTypeConstant.DECIMAL)) {
      row.setField(i, new java.math.BigDecimal(dataMap(column.colName)))
    } else {
      row.setField(i, dataMap(column.colName))
    }
  }

  def genDataMap(objectNode: ObjectNode, field: String): mutable.Map[String, String] = {
    val data: util.Iterator[util.Map.Entry[String, JsonNode]] = objectNode.get("value").get("payload").get(field).fields()
    val dataMap: mutable.Map[String, String] = scala.collection.mutable.Map[String, String]()
    while (data.hasNext) {
      val value: util.Map.Entry[String, JsonNode] = data.next()
      dataMap += (value.getKey -> value.getValue.asText())
    }
    dataMap
  }

}
