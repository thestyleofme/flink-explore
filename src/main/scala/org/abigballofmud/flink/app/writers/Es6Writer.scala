package org.abigballofmud.flink.app.writers

import java.util

import com.typesafe.scalalogging.Logger
import org.abigballofmud.flink.app.constansts.CommonConstant
import org.abigballofmud.flink.app.model.SyncConfig
import org.abigballofmud.flink.app.utils.SyncJdbcUtil
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.connectors.elasticsearch.util.RetryRejectedExecutionFailureHandler
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkBase, ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.http.HttpHost
import org.elasticsearch.action.DocWriteRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import org.slf4j.LoggerFactory

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/04 9:51
 * @since 1.0
 */
//noinspection DuplicatedCode
object Es6Writer {

  private val log = Logger(LoggerFactory.getLogger(Es6Writer.getClass))

  def doWrite(syncConfig: SyncConfig, kafkaStream: DataStream[ObjectNode]): Unit = {
    kafkaStream.addSink(genEsBuilder(syncConfig).build())
  }

  /**
   * es6 集群设置
   *
   * @param syncConfig SyncConfig
   * @return ArrayList[HttpHost]
   */
  def genEs6HttpHosts(syncConfig: SyncConfig): util.ArrayList[HttpHost] = {
    val httpHosts: util.ArrayList[HttpHost] = new java.util.ArrayList[HttpHost]()
    val hosts: Array[String] = syncConfig.syncEs6.httpHosts.split(",")
    for (elem <- hosts) {
      val ipPortArr: Array[String] = elem.trim.split(":")
      httpHosts.add(new HttpHost(ipPortArr(0), ipPortArr(1).toInt, syncConfig.syncEs6.esSchema))
    }
    httpHosts
  }

  /**
   * 往es6写数据
   *
   * @param syncConfig SyncConfig
   * @param element    ObjectNode
   * @param ctx        RuntimeContext
   * @param indexer    RequestIndexer
   */
  def genRequest(syncConfig: SyncConfig, element: ObjectNode, ctx: RuntimeContext, indexer: RequestIndexer): Unit = {
    val map: util.HashMap[String, String] = SyncJdbcUtil.genValueMap(element)
    val splitArr: Array[String] = element.get("value").findValue("pkNames").get(0).asText().split(",")
    if (element.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.INSERT) ||
      element.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.UPDATE)) {
      // 插入 更新
      val indexRequest: IndexRequest = Requests.indexRequest
        .index(syncConfig.syncEs6.esIndex)
        .`type`(syncConfig.syncEs6.esType)
        .id(map.get(splitArr(0)))
        .source(map)
      indexer.add(indexRequest)
      log.info(s"saving data $map to es...")
    } else if (element.get("value").get("type").asText().equalsIgnoreCase(CommonConstant.DELETE)) {
      // 删除
      val deleteRequest: DeleteRequest = Requests.deleteRequest(syncConfig.syncEs6.esIndex)
        .`type`(syncConfig.syncEs6.esType)
        .id(map.get(splitArr(0)))
      indexer.add(deleteRequest)
      log.info(s"delete data $map from es...")
    } else {
      // ignore
    }
  }

  /**
   * 构造esSink
   *
   * @param syncConfig SyncConfig
   * @return ElasticsearchSink.Builder[ObjectNode]
   */
  def genEsBuilder(syncConfig: SyncConfig): ElasticsearchSink.Builder[ObjectNode] = {
    val esBuilder = new ElasticsearchSink.Builder[ObjectNode](
      genEs6HttpHosts(syncConfig),
      new ElasticsearchSinkFunction[ObjectNode] {
        def process(element: ObjectNode, ctx: RuntimeContext, indexer: RequestIndexer) {
          genRequest(syncConfig, element, ctx, indexer)
        }
      }
    )
    // 批量写入时的最大写入条数
    esBuilder.setBulkFlushMaxActions(syncConfig.syncEs6.bulkFlushMaxActions)
    // 开启重试
    esBuilder.setBulkFlushBackoff(true)
    // 多次重试之间的时间间隔为固定常数
    esBuilder.setBulkFlushBackoffType(ElasticsearchSinkBase.FlushBackoffType.CONSTANT)
    // 进行重试的时间间隔
    esBuilder.setBulkFlushBackoffDelay(5000)
    // 失败重试的次数
    esBuilder.setBulkFlushBackoffRetries(3)
    // 失败处理策略
    esBuilder.setFailureHandler(new RetryRejectedExecutionFailureHandler)
    esBuilder
  }

}
