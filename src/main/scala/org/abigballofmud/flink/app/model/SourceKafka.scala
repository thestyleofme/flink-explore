package org.abigballofmud.flink.app.model

/**
 * <p>
 * kafka信息
 * </p>
 *
 * @param kafkaBootstrapServers kafkaBootstrapServers
 * @param kafkaTopic            topic
 * @param initDefaultOffset     初始offset
 * @author isacc 2020/03/06 14:41
 * @since 1.0
 */
case class SourceKafka(kafkaBootstrapServers: String,
                       kafkaTopic: String,
                       initDefaultOffset: String) extends Serializable
