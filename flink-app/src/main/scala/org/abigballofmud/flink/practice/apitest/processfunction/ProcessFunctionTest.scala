package org.abigballofmud.flink.practice.apitest.processfunction

import org.abigballofmud.flink.practice.apitest.source.SensorReading
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 1:04
 * @since 1.0
 */
//noinspection DuplicatedCode
object ProcessFunctionTest {

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

    // 需求：5秒内温度连续上升则报警
    val processedStream: DataStream[String] = dataStream
      .keyBy(_.id)
      .process(new TempIncrAlert())

    dataStream.print("input data")
    processedStream.print("alert data")

    env.execute("process function test")
  }

}

class TempIncrAlert() extends KeyedProcessFunction[String, SensorReading, String] {

  // 定义一个状态，用来保存上一个数据的温度值
  lazy val lastTemp: ValueState[Double] = getRuntimeContext.getState(new ValueStateDescriptor[Double]("lastTemp", classOf[Double]))
  // 当前定时器的ts
  lazy val currentTimer: ValueState[Long] = getRuntimeContext.getState(new ValueStateDescriptor[Long]("currentTimer", classOf[Long]))

  override def processElement(value: SensorReading, ctx: KeyedProcessFunction[String, SensorReading, String]#Context, out: Collector[String]): Unit = {
    // 先取出上一个温度值
    val preTemp: Double = lastTemp.value()
    // 更新温度值
    lastTemp.update(value.temperature)

    val curTimerTs: Long = currentTimer.value()

    if (value.temperature < preTemp || preTemp == 0.0) {
      // 温度下降或第一条数据进来 删除定时器并清空状态
      ctx.timerService().deleteProcessingTimeTimer(curTimerTs)
      currentTimer.clear()
    } else if (value.temperature > preTemp && curTimerTs == 0) {
      // 温度上升且没注册定时器，则注册定时器
      val timerTs: Long = ctx.timerService().currentProcessingTime() + 5000L
      ctx.timerService().registerProcessingTimeTimer(timerTs)
      // 更新当前定时器的ts
      currentTimer.update(timerTs)
    }
  }

  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
    // 输出报警信息
    out.collect(s"${ctx.getCurrentKey} 在5秒内温度持续上升")
    currentTimer.clear()
  }

}
