package org.abigballofmud.flink.practice.app.model

/**
 *
 * <p>
 * 同步到es6的配置信息
 * </p>
 *
 * @param httpHosts           es集群，如: hdsp001:9200,hdsp002:9200,hdsp003:9200
 * @param esSchema            es schema,如 http,https
 * @param esIndex             es index
 * @param bulkFlushMaxActions 批次大小
 * @author isacc 2020/03/06 14:37
 * @since 1.0
 */
case class SyncEs6(httpHosts: String,
                   esSchema: String = "http",
                   esIndex: String,
                   bulkFlushMaxActions: Int = 1) extends Serializable
