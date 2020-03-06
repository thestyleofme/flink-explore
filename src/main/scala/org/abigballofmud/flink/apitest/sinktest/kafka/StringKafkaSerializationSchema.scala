package org.abigballofmud.flink.apitest.sinktest.kafka

import java.lang
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

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
class StringKafkaSerializationSchema(topic: String, partition: Int) extends KafkaSerializationSchema[String] {

  private val random: ThreadLocalRandom = ThreadLocalRandom.current()

  override def serialize(element: String, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    // 随机写到kafka的分区中
    new ProducerRecord(
      topic,
      random.nextInt(partition),
      timestamp,
      UUID.randomUUID().toString.getBytes(StandardCharsets.UTF_8),
      element.getBytes(StandardCharsets.UTF_8))
  }
}
