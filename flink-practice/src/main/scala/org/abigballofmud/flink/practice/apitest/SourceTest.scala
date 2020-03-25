package org.abigballofmud.flink.practice.apitest

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._

import scala.collection.immutable
import scala.util.Random

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/24 15:07
 * @since 1.0
 */
object SourceTest {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // 1. 从集合中读取
    //    val stream1: DataStream[SensorReading] = env.fromCollection(List(
    //      SensorReading("sensor1", 1582530185000L, 26.100025551),
    //      SensorReading("sensor2", 1582530227000L, 24.107895551),
    //      SensorReading("sensor3", 1582530417000L, 27.154552551)
    //    ))

    //    env.fromElements(1, 2.3, "string").print("fromElements").setParallelism(1)

    // 2. 从文件中读取
    //    val stream2: DataStream[String] = env.readTextFile("src/main/resources/sensor.txt")
    //    val stream2_1: DataStream[String] = env.readFile(new TextInputFormat(new Path()),
    //      "file:///E:/myGitCode/flink-explore/src/main/resources/sensor.txt")

    // 3. 从kafka中读
    //    val properties = new Properties()
    //    properties.setProperty("bootstrap.servers", "hdsp001:6667,hdsp002:6667,hdsp003:6667")
    //    val kafkaConsumer: FlinkKafkaConsumerBase[String] = new FlinkKafkaConsumer[String](
    //      "example",
    //      new SimpleStringSchema(),
    //      properties)
    //      .setStartFromLatest()
    //    val kafkaStream: DataStream[String] = env.addSource(kafkaConsumer)

    // 4. 自定义source
    val customSource: DataStream[SensorReading] = env.addSource(new SensorSource())

    //    stream1.print("stream1").setParallelism(1)
    //    stream2.print("stream2").setParallelism(1)
    //    stream2_1.print("stream2_1").setParallelism(1)
    //    kafkaStream.print("kafkaSource").setParallelism(1)
    customSource.print("customSource").setParallelism(1)

    env.execute("source test")
  }
}

case class SensorReading(id: String,
                         timestamp: Long,
                         temperature: Double
                        ) extends Serializable

class SensorSource() extends SourceFunction[SensorReading] {

  /**
   * 标识数据源是否正常运行
   */
  private var isRunning: Boolean = true


  /**
   * 正常生成数据
   *
   * @param ctx SourceFunction.SourceContext
   */
  override def run(ctx: SourceFunction.SourceContext[SensorReading]): Unit = {
    val random: Random = new Random()
    // 初始化定义一组传感器温度数据
    var curTemp: immutable.IndexedSeq[(String, Double)] = 1.to(100000).map(
      i => ("sensor_" + i, 60 + random.nextGaussian() * 20)
    )
    // 产生数据
    while (isRunning) {
      // 在前一次温度的基础上更新温度值
      curTemp = curTemp.map(
        t => (t._1, t._2 + random.nextGaussian())
      )
      // 获取当前时间戳
      val curTime: Long = System.currentTimeMillis()
      curTemp.foreach(
        t => ctx.collect(SensorReading(t._1, curTime, t._2))
      )
      // 设置时间间隔
      Thread.sleep(500L)
    }
  }

  /**
   * 取消数据源的生成
   */
  override def cancel(): Unit = {
    isRunning = false
  }
}