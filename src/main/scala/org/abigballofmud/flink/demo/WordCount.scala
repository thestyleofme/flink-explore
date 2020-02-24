package org.abigballofmud.flink.demo

import org.apache.flink.api.scala._

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/19 14:50
 * @since 1.0
 */
object WordCount {

  def main(args: Array[String]): Unit = {
    // 创建一个批处理的执行环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 从文件读取数据
    val inputDs: DataSet[String] = env.readTextFile("src/main/resources/wordcount.txt")
    // word count
    val wordCount: AggregateDataSet[(String, Int)] = inputDs.flatMap(_.split(" "))
      .map((_, 1))
      .groupBy(0)
      .sum(1)
    // 打印
    wordCount.print()
  }

}
