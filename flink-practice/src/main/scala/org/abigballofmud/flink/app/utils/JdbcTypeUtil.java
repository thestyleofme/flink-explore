package org.abigballofmud.flink.app.utils;

import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 字段类型转jdbcType工具类
 * </p>
 *
 * @author isacc 2020/02/27 15:49
 * @since 1.0
 */
public class JdbcTypeUtil {
    private JdbcTypeUtil() {
    }

    private static final Map<String, Integer> SQL_TYPE_NAMES;

    static {
        Map<String, Integer> names = new HashMap<>(32);
        names.put("CHAR", Types.CHAR);
        names.put("VARCHAR", Types.VARCHAR);
        names.put("BOOLEAN", Types.BOOLEAN);
        names.put("BIT", Types.BOOLEAN);
        names.put("BLOB", Types.BLOB);
        names.put("TINYINT", Types.TINYINT);
        names.put("SMALLINT", Types.SMALLINT);
        names.put("BIGINT", Types.BIGINT);
        names.put("INT", Types.INTEGER);
        names.put("INTEGER", Types.INTEGER);
        names.put("FLOAT", Types.FLOAT);
        names.put("DOUBLE", Types.DOUBLE);
        names.put("DATETIME", Types.TIMESTAMP);
        names.put("DATE", Types.DATE);
        names.put("TIME", Types.TIME);
        names.put("TIMESTAMP", Types.TIMESTAMP);
        names.put("DECIMAL", Types.DECIMAL);
        names.put("BINARY", Types.BINARY);
        SQL_TYPE_NAMES = Collections.unmodifiableMap(names);
    }

    public static Integer getSqlType(String colType) {
        // 默认为string
        return SQL_TYPE_NAMES.getOrDefault(colType.toUpperCase().trim(), Types.VARCHAR);
    }

}
