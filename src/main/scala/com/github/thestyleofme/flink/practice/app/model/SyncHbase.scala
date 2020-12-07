package com.github.thestyleofme.flink.practice.app.model

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/12 14:19
 * @since 1.0
 */
case class SyncHbase(quorum: String,
                     rootDir: String,
                     znodeParent: String,
                     namespace: String,
                     table: String,
                     columns: java.util.List[Column],
                     batchSize: Int = 1) extends Serializable

/**
 * 该列簇下有哪几个字段，每个字段只能一个列簇
 *
 * @param family 列簇
 * @param cols   字段
 */
case class Column(family: String,
                  cols: java.util.List[String]) extends Serializable