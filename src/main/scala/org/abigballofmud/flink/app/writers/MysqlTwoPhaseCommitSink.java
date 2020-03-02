package org.abigballofmud.flink.app.writers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jdk.nashorn.internal.ir.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.app.utils.DbConnectUtil;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/02 0:31
 * @since 1.0
 */
@Slf4j
public class MysqlTwoPhaseCommitSink extends TwoPhaseCommitSinkFunction<ObjectNode, Connection, Void> {

    private final String sql;
    private final String url;
    private final String username;
    private final String password;

    public MysqlTwoPhaseCommitSink(String sql, String url, String username, String password) {
        super(new KryoSerializer<>(Connection.class, new ExecutionConfig()), VoidSerializer.INSTANCE);
        this.sql = sql;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    protected void invoke(Connection transaction, ObjectNode value, Context context) {
        log.info("start transaction...");
        // 插入 更新 删除
        try (PreparedStatement preparedStatement = transaction.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("sql execute error,", e);
        }
    }

    @Override
    protected Connection beginTransaction() {
        return DbConnectUtil.getConnection(url, username, password);
    }

    @Override
    protected void preCommit(Connection transaction) {
        log.info("start preCommit...");
    }

    /**
     * 如果invoke方法执行正常，则提交事务
     *
     * @param transaction Connection
     */
    @Override
    protected void commit(Connection transaction) {
        log.info("start commit...");
        DbConnectUtil.commit(transaction);
    }

    /**
     * 如果invoke执行异常则回滚事务，下一次的checkpoint操作不会执行
     *
     * @param transaction Connection
     */
    @Override
    protected void abort(Connection transaction) {
        log.info("start abort rollback...");
        DbConnectUtil.rollback(transaction);
    }
}
