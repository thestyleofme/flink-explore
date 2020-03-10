package org.abigballofmud.flink.app.model

/**
 * <p>
 * 写入hdfs
 * </p>
 *
 * @param hdfsPath        写入到hdfs的路径，即hive表所在路径
 * @param fileType        hive的表类型，text/parquet/
 * @param isPartition     是否分区，默认不分区
 * @param partitionName   时间分区字段名称，这里默认是yyyy-MM-dd，将每日数据做一个分区
 * @param fieldsDelimited 若为text格式，指定ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
 * @param partPrefix      写入的hdfs文件前缀
 * @param partSuffix      写入的hdfs文件后缀
 * @author isacc 2020/03/06 14:44
 * @since 1.0
 */
case class SyncHive(hdfsPath: String,
                    fileType: String,
                    isPartition: Boolean = false,
                    partitionName: String,
                    fieldsDelimited: String,
                    partPrefix: String,
                    partSuffix: String
                   ) extends Serializable
