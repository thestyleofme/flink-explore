package org.abigballofmud.flink.practice.apitest.sinktest.jdbc

import java.sql.{Connection, DriverManager, PreparedStatement}
import java.util.Objects

import org.abigballofmud.flink.practice.apitest.{SensorReading, SensorSource}
import org.abigballofmud.flink.practice.apitest.SensorReading
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/25 10:04
 * @since 1.0
 */
object JdbcSinkTest {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(8)

    //    val stream: DataStream[String] = env.readTextFile("src/main/resources/sensor.txt")
    //
    //    val dataStream: DataStream[SensorReading] = stream.map(data => {
    //      val splitArr: Array[String] = data.split(",")
    //      SensorReading(splitArr(0).trim, splitArr(1).trim.toLong, splitArr(2).trim.toDouble)
    //    })

    val dataStream: DataStream[SensorReading] = env.addSource(new SensorSource())

    dataStream.addSink(new MyJdbcSink())

    //    dataStream.print()

    env.execute("my jdbc sink test")
  }

}

class MyJdbcSink() extends RichSinkFunction[SensorReading] {

  private var conn: Connection = _
  private var insertStmt: PreparedStatement = _
  private var updateStmt: PreparedStatement = _

  // 在初始化中创建连接
  override def open(parameters: Configuration): Unit = {
    conn = DriverManager.getConnection(
      "jdbc:mysql://dev.hdsp.hand.com:7233/hdsp_test?useUnicode=true&characterEncoding=utf-8&useSSL=false",
      "hdsp_dev",
      "hdsp_dev")
    insertStmt = conn.prepareStatement("INSERT INTO `temperature`(`sensor`,`temp`) VALUES(?, ?)")
    updateStmt = conn.prepareStatement("UPDATE `temperature` SET temp = ? WHERE sensor = ?")
  }

  override def invoke(value: SensorReading, context: SinkFunction.Context[_]): Unit = {
    // 先做更新
    updateStmt.setDouble(1, value.temperature)
    updateStmt.setString(2, value.id)
    updateStmt.execute()
    // 没有更新才做插入
    if (updateStmt.getUpdateCount == 0) {
      insertStmt.setString(1, value.id)
      insertStmt.setDouble(2, value.temperature)
      insertStmt.execute()
    }
  }

  override def close(): Unit = {
    if (Objects.nonNull(insertStmt)) {
      insertStmt.close()
    }
    if (Objects.nonNull(updateStmt)) {
      updateStmt.close()
    }
    if (Objects.nonNull(conn)) {
      conn.close()
    }
  }
}


