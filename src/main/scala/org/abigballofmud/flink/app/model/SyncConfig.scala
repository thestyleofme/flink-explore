package org.abigballofmud.flink.app.model

/**
 * <p>
 * 实时数仓flink执行配置case类
 * </p>
 *
 * @author isacc 2020/02/26 16:55
 * @since 1.0
 */
case class SyncConfig(syncFlink: SyncFlink,
                      sourceKafka: SourceKafka,
                      syncJdbc: SyncJdbc,
                      syncKafka: SyncKafka,
                      syncEs6: SyncEs6,
                      syncRedis: SyncRedis,
                      syncHive: SyncHive
                     ) extends Serializable
