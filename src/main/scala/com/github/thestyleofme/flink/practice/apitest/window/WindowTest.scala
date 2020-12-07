package com.github.thestyleofme.flink.practice.apitest.window

import com.github.thestyleofme.flink.practice.apitest.source.SensorReading
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.functions.{AssignerWithPeriodicWatermarks, AssignerWithPunctuatedWatermarks}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * <p>
 * nc -lk 9999
 * </p>
 *
 * @author isacc 2020/03/25 0:14
 * @since 1.0
 */
//noinspection DuplicatedCode
object WindowTest {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(100L)

    val stream: DataStream[String] = env.socketTextStream("hdsp001", 9999)
    val dataStream: DataStream[SensorReading] = stream
      .map(data => {
        val splitArr: Array[String] = data.split(",")
        SensorReading(splitArr(0).trim, splitArr(1).trim.toLong, splitArr(2).trim.toDouble)
      })
      //      .assignAscendingTimestamps(_.timestamp) // 来的数据是升序的话
      //      .assignTimestampsAndWatermarks(new MyPeriodicWatermarksAssigner())
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(1)) {
        override def extractTimestamp(element: SensorReading): Long = {
          element.timestamp
        }
      })

    // 统计15秒内最小的温度，隔5s输出一次
    val minTemperaturePerWindowStream: DataStream[(String, Double)] = dataStream
      .map(data => (data.id, data.temperature))
      .keyBy(_._1)
      // 可以去了解下滑动/滚动窗口的计算公式
      // org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows.assignWindows
      // org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows.assignWindows
      .window(SlidingEventTimeWindows.of(Time.seconds(15), Time.seconds(5), Time.hours(-8)))
      //      .timeWindow(Time.seconds(15), Time.seconds(5)) // 开窗
      .reduce((data1, data2) => (data1._1, data1._2.min(data2._2))) // reduce做增量聚合

    minTemperaturePerWindowStream.print("min temp")
    dataStream.print("input data")

    env.execute("window test")

  }
}

/**
 * 周期性的Watermarks
 */
class MyPeriodicWatermarksAssigner() extends AssignerWithPeriodicWatermarks[SensorReading] {

  // 延迟1分钟
  val bound: Long = 60 * 1000
  // 当前watermark的值
  var maxTs: Long = Long.MinValue

  override def getCurrentWatermark: Watermark = {
    new Watermark(maxTs - bound)
  }

  override def extractTimestamp(element: SensorReading, previousElementTimestamp: Long): Long = {
    maxTs = maxTs.max(element.timestamp)
    element.timestamp
  }

}

class MyPunctuatedWatermarksAssigner() extends AssignerWithPunctuatedWatermarks[SensorReading] {

  override def checkAndGetNextWatermark(lastElement: SensorReading, extractedTimestamp: Long): Watermark = {
    new Watermark(extractedTimestamp)
  }

  override def extractTimestamp(element: SensorReading, previousElementTimestamp: Long): Long = {
    element.timestamp
  }
}
