package com.github.thestyleofme.flink.practice.app.model

/**
 * <p>
 * flink执行配置类
 * </p>
 *
 * @param jobName flink的jobName
 * @author isacc 2020/03/06 14:42
 * @since 1.0
 */
case class SyncFlink(jobName: String,
                     sourceSchema: String,
                     sourceTable: String,
                     checkPointPath: String,
                     writeType: String) extends Serializable
