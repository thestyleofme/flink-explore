package org.abigballofmud.flink.app.model

/**
 * <p>
 * 写入kafka的信息
 * </p>
 *
 * @param kafkaBootstrapServers kafkaBootstrapServers
 * @param kafkaTopic            topic
 * @author isacc 2020/03/06 14:40
 * @since 1.0
 */
case class SyncKafka(kafkaBootstrapServers: String,
                     kafkaTopic: String) extends Serializable
