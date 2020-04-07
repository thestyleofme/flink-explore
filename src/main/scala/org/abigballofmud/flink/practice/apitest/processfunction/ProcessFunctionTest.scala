package org.abigballofmud.flink.practice.apitest.processfunction

import org.abigballofmud.flink.practice.apitest.source.SensorReading
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
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

    // 需求：温度骤变触发报警
    val processedStream2: DataStream[(String, Double, Double)] = dataStream
      .keyBy(_.id)
      .flatMap(new TempChangeAlert(10.0))
    //      .process(new TempChangeAlert2(10.0))

    // 改写上面的flatMap
    val processedStream3: DataStream[(String, Double, Double)] = dataStream
      .keyBy(_.id)
      .flatMapWithState[(String, Double, Double), Double] {
        // 如果没有状态的话，也就是没有数据来过，那么就将当前数据温度值存入状态
        case (input: SensorReading, None) => (List.empty, Some(input.temperature))
        // 如果有状态，就应该与上次的温度值比较差值，如果大于阈值就输出报警
        case (input: SensorReading, lastTemp: Some[Double]) =>
          val diff: Double = input.temperature - lastTemp.get.abs
          if (diff > 10.0) {
            (List((input.id, lastTemp.get, input.temperature)), Some(input.temperature))
          } else {
            (List.empty, Some(input.temperature))
          }
      }

    dataStream.print("input data")
    //    processedStream.print("incr data")
    processedStream2.print("change data")

    env.execute("process function test")
  }

}

//noinspection DuplicatedCode
class TempChangeAlert(threshold: Double) extends RichFlatMapFunction[SensorReading, (String, Double, Double)] {

  // 定义一个状态，用来保存上一个数据的温度值
  private var lastTempState: ValueState[Double] = _


  override def open(parameters: Configuration): Unit = {
    lastTempState = getRuntimeContext.getState(new ValueStateDescriptor[Double]("lastTemp", classOf[Double]))
  }

  override def flatMap(value: SensorReading, out: Collector[(String, Double, Double)]): Unit = {
    // 先取出上一个温度值
    val lastTemp: Double = lastTempState.value()
    // 当前的温度值和上次的求差，如果大于阈值，输出报警信息
    // 忽略第一条数据的bug
    val diff: Double = (value.temperature - lastTemp).abs
    if (diff > threshold) {
      out.collect((value.id, lastTemp, value.temperature))
    }
    // 更新状态
    lastTempState.update(value.temperature)
  }
}

//noinspection DuplicatedCode
class TempChangeAlert2(threshold: Double) extends KeyedProcessFunction[String, SensorReading, (String, Double, Double)] {

  // 定义一个状态，用来保存上一个数据的温度值
  lazy val lastTempState: ValueState[Double] = getRuntimeContext.getState(new ValueStateDescriptor[Double]("lastTemp", classOf[Double]))

  override def processElement(value: SensorReading, ctx: KeyedProcessFunction[String, SensorReading, (String, Double, Double)]#Context, out: Collector[(String, Double, Double)]): Unit = {
    // 先取出上一个温度值
    val lastTemp: Double = lastTempState.value()
    // 当前的温度值和上次的求差，如果大于阈值，输出报警信息
    // 忽略第一条数据的bug
    val diff: Double = (value.temperature - lastTemp).abs
    if (diff > threshold) {
      out.collect((value.id, lastTemp, value.temperature))
    }
    // 更新状态
    lastTempState.update(value.temperature)
  }
}

//noinspection DuplicatedCode
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

