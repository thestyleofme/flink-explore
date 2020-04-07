package org.abigballofmud.flink.practice.app.udf.hbase;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.practice.app.model.Column;
import org.abigballofmud.flink.practice.app.model.SyncConfig;
import org.abigballofmud.flink.practice.app.utils.SyncJdbcUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

/**
 * <p>
 * hbase常用命令
 * https://blog.csdn.net/guolindonggld/article/details/82767620
 * <p>
 * 创建namespace
 * create_namespace 'isacc'
 * 查看该namespace下的table
 * list_namespace_tables 'isacc'
 * 创建表
 * create 'isacc:dev_test', 'f1'
 * 删除表需先disable
 * disable 'isacc:dev_test'
 * drop 'isacc:dev_test'
 * </p>
 *
 * @author isacc 2020/03/12 14:08
 * @since 1.0
 */
@Slf4j
public class HBaseSink extends RichSinkFunction<ObjectNode> {

    private SyncConfig syncConfig;
    private transient Connection connection;
    private transient BufferedMutator mutator;
    private int count = 0;

    public HBaseSink(SyncConfig syncConfig) {
        this.syncConfig = syncConfig;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        // 默认使用hbase用户
        System.setProperty("HADOOP_USER_NAME", "hbase");
        configuration.set("hbase.zookeeper.quorum", syncConfig.syncHbase().quorum());
        configuration.set("hbase.rootdir", syncConfig.syncHbase().rootDir());
        configuration.set("zookeeper.znode.parent", syncConfig.syncHbase().znodeParent());
        connection = ConnectionFactory.createConnection(configuration);
        BufferedMutatorParams params = new BufferedMutatorParams(
                TableName.valueOf(syncConfig.syncHbase().namespace(), syncConfig.syncHbase().table()));
        params.writeBufferSize(2 * 1024 * 1024L);
        mutator = connection.getBufferedMutator(params);
    }

    @Override
    public void invoke(ObjectNode data, Context context) throws Exception {
        count++;
        HashMap<String, String> valueMap = SyncJdbcUtil.genValueMap(data);
        String[] pkArr = data.get("value").findValue("pkNames").get(0).asText().split(",");
        long ts = data.get("value").get("ts").asLong();
        List<Column> columnList = syncConfig.syncHbase().columns();
        Put put = new Put(pkArr[0].getBytes(), ts);
        columnList.forEach(column -> valueMap.forEach((k, v) -> {
            if (column.cols().contains(k)) {
                put.addColumn(column.family().getBytes(), k.getBytes(), v.getBytes());
            }
        }));
        mutator.mutate(put);
        // 到达批次大小 刷新一下数据
        if (count >= syncConfig.syncHbase().batchSize()) {
            mutator.flush();
            count = 0;
        }
    }

    @Override
    public void close() throws Exception {
        if (Objects.nonNull(mutator)) {
            mutator.close();
        }
        if (Objects.nonNull(connection)) {
            connection.close();
        }
    }
}
