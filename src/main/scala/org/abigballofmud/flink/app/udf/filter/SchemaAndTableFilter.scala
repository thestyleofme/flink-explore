package org.abigballofmud.flink.app.udf.filter

import org.abigballofmud.flink.app.model.SyncConfig
import org.apache.flink.api.common.functions.RichFilterFunction
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode

/**
 * <p>
 * 自定义FilterFunction
 * </p>
 *
 * @author isacc 2020/02/28 8:58
 * @since 1.0
 */
class SchemaAndTableFilter(syncConfig: SyncConfig) extends RichFilterFunction[ObjectNode] {
  override def filter(value: ObjectNode): Boolean = {
    value.get("value").get("database").asText().equalsIgnoreCase(syncConfig.syncFlink.sourceSchema) &&
      value.get("value").get("table").asText().equalsIgnoreCase(syncConfig.syncFlink.sourceTable)
  }
}
