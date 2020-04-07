package org.abigballofmud.flink.practice.app.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/18 17:12
 * @since 1.0
 */
public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("util class");
    }

   public static LocalDateTime timestamp2LocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
}
