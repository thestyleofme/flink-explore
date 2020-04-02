package org.abigballofmud.flink.practice.apitest.sinktest.es

import java.util

import org.abigballofmud.flink.practice.apitest.source.SensorReading
import org.abigballofmud.flink.practice.apitest.source.SensorReading
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests

/**
 * <p>
 * es查看index
 * http://hdsp003:9200/_cat/indices?v
 * es查看index下的数据，这里的sensor即index
 * http://hdsp003:9200/sensor/_search?pretty
 * 删除index
 * curl -X DELETE "hdsp003:9200/sensor?pretty"
 * </p>
 *
 * @author isacc 2020/03/03 1:19
 * @since 1.0
 */
//noinspection DuplicatedCode
object EsSinkTest {

  def main(args: Array[String]): Unit = {

    // es 集群设置
    val httpHosts: util.ArrayList[HttpHost] = new java.util.ArrayList[HttpHost]()
    // 这里只有一台
    httpHosts.add(new HttpHost("hdsp003", 9200, "http"))
    val esSinkBuilder = new ElasticsearchSink.Builder[SensorReading](
      httpHosts,
      new ElasticsearchSinkFunction[SensorReading] {
        def process(element: SensorReading, ctx: RuntimeContext, indexer: RequestIndexer) {
          // 写入的数据封装
          val json = new java.util.HashMap[String, String]
          json.put("sensor_id", element.id)
          json.put("timestamp", element.timestamp.toString)
          json.put("temperature", element.temperature.toString)
          // 创建indexRequest 这里可新增 修改 删除
          val indexRequest: IndexRequest = Requests.indexRequest
            .index("sensor")
            .`type`("data")
            .source(json)
          // 发出http请求
          indexer.add(indexRequest)
          println(s"save data $element to es success")
        }
      }
    )

    // configuration for the bulk requests;
    // this instructs the sink to emit after every element, otherwise they would be buffered
    esSinkBuilder.setBulkFlushMaxActions(1)

    // provide a RestClientFactory for custom configuration on the internally created REST client
    //        esSinkBuilder.setRestClientFactory(
    //          restClientBuilder -> {
    //            restClientBuilder.setDefaultHeaders(...)
    //            restClientBuilder.setMaxRetryTimeoutMillis(...)
    //            restClientBuilder.setPathPrefix(...)
    //            restClientBuilder.setHttpClientConfigCallback(...)
    //          }
    //        )

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val stream: DataStream[String] = env.readTextFile("src/main/resources/sensor.txt")

    val dataStream: DataStream[SensorReading] = stream.map(data => {
      val splitArr: Array[String] = data.split(",")
      SensorReading(splitArr(0).trim, splitArr(1).trim.toLong, splitArr(2).trim.toDouble)
    })

    dataStream.addSink(esSinkBuilder.build())

    env.execute("es sink test")
  }
}
