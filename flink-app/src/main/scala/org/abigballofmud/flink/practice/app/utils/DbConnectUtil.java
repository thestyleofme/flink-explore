package org.abigballofmud.flink.practice.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/02 0:52
 * @since 1.0
 */
@Slf4j
public class DbConnectUtil {

    private DbConnectUtil() {
    }

    /**
     * 获取连接
     *
     * @param url      jdbc url
     * @param user     username
     * @param password password
     * @return Connection
     */
    public static Connection getConnection(String url, String user, String password) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            log.info("获取连接成功...");
            // 设置手动提交
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            log.error("获取连接失败，url: {}, user: {}, password: {}", url, user, password);
        }
        return conn;
    }

    /**
     * 提交事务
     *
     * @param conn Connection
     */
    public static void commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                log.error("提交事务失败,", e);
            } finally {
                close(conn);
            }
        }
    }

    /**
     * 事务回滚
     *
     * @param conn Connection
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("事务回滚失败", e);
            } finally {
                close(conn);
            }
        }
    }

    /**
     * 关闭连接
     *
     * @param conn Connection
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("关闭连接失败,", e);
            }
        }
    }

}
