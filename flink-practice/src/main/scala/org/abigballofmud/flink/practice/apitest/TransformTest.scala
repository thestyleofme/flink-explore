package org.abigballofmud.flink.practice.apitest

import org.apache.flink.api.common.functions.{FilterFunction, RichFilterFunction}
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/27 0:15
 * @since 1.0
 */
object TransformTest {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val streamFromFile: DataStream[String] = env.readTextFile("src/main/resources/sensor.txt")

    // 1. 基本转换算子和简单聚合算子
    val dataStream: DataStream[SensorReading] = streamFromFile.map(data => {
      val dataArr: Array[String] = data.split(",")
      SensorReading(dataArr(0).trim, dataArr(1).trim.toLong, dataArr(2).trim.toDouble)
    })
    //      .keyBy(0)
    //      .keyBy(_.id)
    //      .sum(2)
    // 输出 当前传感器的温度+10 时间戳是上一次的时间+1
    // x是上一次聚合后的SensorReading y是新来的SensorReading
    //      .reduce((x, y) => SensorReading(x.id, x.timestamp + 1, y.temperature + 10))

    // 2. 多流转换算子
    //    dataStream.print()
    // 分流
    //    val highOutPutTag: OutputTag[SensorReading] = new OutputTag[SensorReading]("high")
    //    val lowOutPutTag: OutputTag[SensorReading] = new OutputTag[SensorReading]("low")
    //    val processedDataStream: DataStream[SensorReading] = dataStream.process(new ProcessFunction[SensorReading, SensorReading] {
    //      override def processElement(value: SensorReading, ctx: ProcessFunction[SensorReading, SensorReading]#Context, out: Collector[SensorReading]): Unit = {
    //        if (value.temperature > 30) {
    //          ctx.output(highOutPutTag, value)
    //        } else if (value.temperature >= 0 && value.temperature <= 30) {
    //          ctx.output(lowOutPutTag, value)
    //        } else {
    //          // 其他
    //          out.collect(value)
    //        }
    //      }
    //    })
    //    val highDataStream: DataStream[SensorReading] = processedDataStream.getSideOutput(highOutPutTag)
    //    val lowDataStream: DataStream[SensorReading] = processedDataStream.getSideOutput(lowOutPutTag)
    //    highDataStream.print("high")
    //    lowDataStream.print("low")

    // 合并两条流
    //    val warning: DataStream[(String, Double)] = highDataStream.map(data => (data.id, data.temperature))
    //    val connectedStream: ConnectedStreams[(String, Double), SensorReading] = warning.connect(lowDataStream)
    //    val coMapDataStream: DataStream[Product] = connectedStream.map(
    //      warningData => (warningData._1, warningData._2, "warning"),
    //      lowData => (lowData.id, "healthy")
    //    )
    //    coMapDataStream.print()

    // 使用union可以合并多个数据类型一致的流
    //    val unionStream: DataStream[SensorReading] = highDataStream.union(lowDataStream)
    //    unionStream.print("union")

    // 3. UDF
    //    dataStream.filter(new MyFilter("sensor_1")).print()
    //    dataStream.filter(value => value.id.startsWith("sensor_1")).print()
    //    dataStream.filter(_.id.startsWith("sensor_1")).print()
    //    dataStream.filter(new RichFilterFunction[SensorReading] {
    //      override def filter(value: SensorReading): Boolean = {
    //        value.id.startsWith("sensor_1")
    //      }
    //    }).print()

    env.execute("transform test")
  }

}

// 自定义有个好处就是这里还可以传参
class MyFilter(keyWord: String) extends FilterFunction[SensorReading] {
  override def filter(value: SensorReading): Boolean = {
    value.id.startsWith(keyWord)
  }
}
