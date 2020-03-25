package org.abigballofmud.flink.app.udf.filter

import org.abigballofmud.flink.app.constansts.KafkaSourceFrom
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
    if (KafkaSourceFrom.CANAL.equalsIgnoreCase(syncConfig.sourceKafka.sourceFrom)) {
      value.get("value").get("database").asText().equalsIgnoreCase(syncConfig.syncFlink.sourceSchema) &&
        value.get("value").get("table").asText().equalsIgnoreCase(syncConfig.syncFlink.sourceTable)
    } else if (KafkaSourceFrom.ORACLE_KAFKA_CONNECTOR.equalsIgnoreCase(syncConfig.sourceKafka.sourceFrom)) {
      value.get("value").get("payload").get("SEG_OWNER").asText().equalsIgnoreCase(syncConfig.syncFlink.sourceSchema) &&
        value.get("value").get("payload").get("TABLE_NAME").asText().equalsIgnoreCase(syncConfig.syncFlink.sourceTable)
    } else {
      false
    }

  }
}
