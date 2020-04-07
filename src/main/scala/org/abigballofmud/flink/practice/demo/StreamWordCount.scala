package org.abigballofmud.flink.practice.demo

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._


/**
 * <p>
 * nc -lk 9999
 * 传参示例 --host hdsp001 --port 9999
 * </p>
 *
 * @author isacc 2020/02/19 14:50
 * @since 1.0
 */
object StreamWordCount {

  def main(args: Array[String]): Unit = {

    // 从外部传入参数
    val parameterTool: ParameterTool = ParameterTool.fromArgs(args)
    val host: String = parameterTool.get("host")
    val port: Int = parameterTool.getInt("port")

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)

    val dataStream: DataStream[String] = env.socketTextStream(host, port)
    val wordCountDs: DataStream[(String, Int)] = dataStream
      .flatMap(_.split("\\W+"))
      .filter(_.nonEmpty)
      .map((_, 1))
      .keyBy(0)
      .sum(1)
    wordCountDs.print().setParallelism(8)
    // 启动flink，执行任务
    env.execute("flink_stream_wc")
  }

}
