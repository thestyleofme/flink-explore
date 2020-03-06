package org.abigballofmud.flink.apitest.tableapi

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{EnvironmentSettings, TableEnvironment}
import org.apache.flink.table.api.scala._

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/05 16:17
 * @since 1.0
 */
object TableTest {

  def main(args: Array[String]): Unit = {
    // **********************
    // BLINK STREAMING QUERY
    // **********************
    val bsEnv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val bsSettings: EnvironmentSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
    val bsTableEnv: StreamTableEnvironment = StreamTableEnvironment.create(bsEnv, bsSettings)
    // or val bsTableEnv: TableEnvironment = TableEnvironment.create(bsSettings)


    bsEnv.execute()
  }
}
