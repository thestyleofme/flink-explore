package org.abigballofmud.flink.app.utils

import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util
import java.util.{Date, Objects}

import org.abigballofmud.flink.app.model.{JdbcTypeUtil, QueryAndSqlType, SyncConfig}
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
 * @author isacc 2020/02/25 23:45
 * @since 1.0
 */
//noinspection DuplicatedCode
object CommonUtil {

  /**
   * 根据query语句生成sqlTypes
   *
   * @param syncConfig SyncConfig
   */
  def genSqlTypes(syncConfig: SyncConfig): Unit = {
    val deleteSqlTypes: Array[Int] = genSqlType(syncConfig, syncConfig.syncJdbc.delete)
    syncConfig.syncJdbc.delete.sqlTypes = deleteSqlTypes
    val updateSqlTypes: Array[Int] = genSqlType(syncConfig, syncConfig.syncJdbc.update)
    syncConfig.syncJdbc.update.sqlTypes = updateSqlTypes
    val insertSqlTypes: Array[Int] = genSqlType(syncConfig, syncConfig.syncJdbc.insert)
    syncConfig.syncJdbc.insert.sqlTypes = insertSqlTypes
  }

  /**
   * 组装sqlType
   *
   * @param syncConfig SyncConfig
   * @param queryAndSqlType QueryAndSqlType
   * @return Array[Int]
   */
  def genSqlType(syncConfig: SyncConfig, queryAndSqlType: QueryAndSqlType): Array[Int] = {
    val colTypeArr: Array[String] = queryAndSqlType.colTypes.split(",")
    val sqlTypes = new Array[Int](colTypeArr.length)
    for (i <- colTypeArr.indices) {
      sqlTypes(i) = JdbcTypeUtil.getSqlType(colTypeArr(i).trim)
    }
    sqlTypes
  }

  def getCanalDataMap(objectNode: ObjectNode): mutable.Map[String, String] = {
    val data: util.Iterator[util.Map.Entry[String, JsonNode]] = objectNode.get("value").findValue("data").get(0).fields()
    val dataMap: mutable.Map[String, String] = scala.collection.mutable.Map[String, String]()
    while (data.hasNext) {
      val value: util.Map.Entry[String, JsonNode] = data.next()
      dataMap += (value.getKey -> value.getValue.asText())
    }
    dataMap
  }

  def canalCommonInfo(objectNode: ObjectNode): (mutable.Map[String, String], Array[String], util.Iterator[util.Map.Entry[String, JsonNode]]) = {
    // 获取canal topic中的data 即字段的值
    val dataMap: mutable.Map[String, String] = getCanalDataMap(objectNode)
    // 根据字段类型组装Row
    val splitArr: Array[String] = objectNode.get("value").findValue("pkNames").get(0).asText().split(",")
    val mysqlType: util.Iterator[util.Map.Entry[String, JsonNode]] = objectNode.get("value").get("mysqlType").fields()
    (dataMap, splitArr, mysqlType)
  }

  /**
   *
   * canal中update转Row
   *
   * @return MapFunction[ObjectNode, Row]
   */
  def canalUpdateTransformToRow: MapFunction[ObjectNode, Row] = {
    new MapFunction[ObjectNode, Row]() {
      override def map(objectNode: ObjectNode): Row = {
        val tuple: (mutable.Map[String, String], Array[String], util.Iterator[util.Map.Entry[String, JsonNode]]) = canalCommonInfo(objectNode)
        val row = new Row(tuple._1.size)
        var i: Int = 0
        // 先组装需要set的字段
        while (tuple._3.hasNext) {
          val entry: util.Map.Entry[String, JsonNode] = tuple._3.next()
          if (tuple._1.contains(entry.getKey) && !tuple._2.contains(entry.getKey)) {
            // 根据字段类型去设置row的field
            CommonUtil.setField(row, i, tuple._1, null, entry)
            i += 1
          }
        }
        // 在组装主键
        val mysqlType: util.Iterator[util.Map.Entry[String, JsonNode]] = objectNode.get("value").get("mysqlType").fields()
        while (mysqlType.hasNext) {
          val entry: util.Map.Entry[String, JsonNode] = mysqlType.next()
          if (tuple._1.contains(entry.getKey) && tuple._2.contains(entry.getKey)) {
            CommonUtil.setField(row, i, tuple._1, null, entry)
          }
          i += 1
        }
        row
      }
    }
  }

  /**
   *
   * canal中delete转Row
   *
   * @return MapFunction[ObjectNode, Row]
   */
  def canalDeleteTransformToRow: MapFunction[ObjectNode, Row] = {
    new MapFunction[ObjectNode, Row]() {
      override def map(objectNode: ObjectNode): Row = {
        val tuple: (mutable.Map[String, String], Array[String], util.Iterator[util.Map.Entry[String, JsonNode]]) = canalCommonInfo(objectNode)
        val row = new Row(tuple._2.length)
        while (tuple._3.hasNext) {
          val entry: util.Map.Entry[String, JsonNode] = tuple._3.next()
          if (tuple._1.contains(entry.getKey)) {
            // 根据字段类型去设置row的field
            for (j <- tuple._2.indices) {
              if (tuple._1.contains(tuple._2(j))) {
                // 根据字段类型去设置row的field
                CommonUtil.setField(row, j, tuple._1, tuple._2(j).trim, entry)
              }
            }
          }
        }
        row
      }
    }
  }

  /**
   *
   * canal中insert转Row
   *
   * @return MapFunction[ObjectNode, Row]
   */
  def canalInsertTransformToRow: MapFunction[ObjectNode, Row] = {
    new MapFunction[ObjectNode, Row]() {
      override def map(objectNode: ObjectNode): Row = {
        val tuple: (mutable.Map[String, String], Array[String], util.Iterator[util.Map.Entry[String, JsonNode]]) = canalCommonInfo(objectNode)
        val row = new Row(tuple._1.size)
        var i: Int = 0
        while (tuple._3.hasNext) {
          val entry: util.Map.Entry[String, JsonNode] = tuple._3.next()
          if (tuple._1.contains(entry.getKey)) {
            // 根据字段类型去设置row的field
            //            row.setField(i, dataMap(entry.getKey))
            CommonUtil.setField(row, i, tuple._1, null, entry)
            i += 1
          }
        }
        row
      }
    }
  }

  /**
   * 根据mysql字段类型去适配java.sql.Types
   *
   * @param row     Row
   * @param index   field的index
   * @param dataMap canal的data值
   * @param pkField 主键
   * @param entry   canal的mysqlType值
   */
  def setField(row: Row, index: Int, dataMap: mutable.Map[String, String], pkField: String, entry: util.Map.Entry[String, JsonNode]): Unit = {
    val col: String = entry.getKey
    if (Objects.nonNull(pkField) && !col.equals(pkField)) {
      // 若传了主键，只给row设置主键的值
      return
    }
    val colType: String = entry.getValue.asText().toUpperCase
    if (colType.contains("CHAR")) {
      //      Types.CHAR
      row.setField(index, dataMap(col))
    } else if (colType.contains("BOOLEAN") || colType.contains("BIT")) {
      //      Types.BOOLEAN
      row.setField(index, java.lang.Boolean.valueOf(dataMap(col)))
    } else if (colType.contains("BLOB") || colType.contains("TINYINT")) {
      //      Types.BINARY
      row.setField(index, dataMap(col).getBytes(StandardCharsets.UTF_8))
    } else if (colType.contains("SMALLINT")) {
      //      Types.SMALLINT
      row.setField(index, java.lang.Short.valueOf(dataMap(col)))
    } else if (colType.contains("BIGINT")) {
      //     Types.BIGINT
      row.setField(index, java.lang.Long.valueOf(dataMap(col)))
    } else if (colType.contains("INT")) {
      //       Types.INTEGER
      row.setField(index, java.lang.Integer.valueOf(dataMap(col)))
    } else if (colType.contains("DOUBLE")) {
      //       Types.DOUBLE
      row.setField(index, java.lang.Double.valueOf(dataMap(col)))
    } else if (colType.contains("DECIMAL")) {
      //       Types.DECIMAL
      row.setField(index, new java.math.BigDecimal(dataMap(col)))
    } else if (colType.equals("DATE")) {
      //       Types.DATE
      val localDate: LocalDate = LocalDate.parse(dataMap(col), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
      row.setField(index, Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant))
    } else if (colType.equals("DATETIME")) {
      //       Types.TIMESTAMP
      val localDateTime: LocalDateTime = LocalDateTime.parse(dataMap(col), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
      row.setField(index, new java.sql.Timestamp(localDateTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli))
    } else if (colType.contains("TIMESTAMP")) {
      //       Types.TIMESTAMP
      row.setField(index, new java.sql.Timestamp(java.lang.Long.valueOf(dataMap(col))))
    } else {
      row.setField(index, dataMap(col))
    }
  }


}
