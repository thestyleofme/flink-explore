package org.abigballofmud.flink.app.udf

import java.lang
import java.nio.charset.StandardCharsets

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/02 15:13
 * @since 1.0
 */
//noinspection DuplicatedCode
class SyncKafkaSerializationSchema(topic: String) extends KafkaSerializationSchema[ObjectNode] {

  override def serialize(element: ObjectNode, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    new ProducerRecord[Array[Byte], Array[Byte]](
      topic,
      null,
      timestamp,
      null,
      element.get("value").toString.getBytes(StandardCharsets.UTF_8))
  }
}
