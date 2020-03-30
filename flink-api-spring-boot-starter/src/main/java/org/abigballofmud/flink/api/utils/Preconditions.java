package org.abigballofmud.flink.api.utils;

import java.util.Objects;
import java.util.stream.Stream;

import org.abigballofmud.flink.api.exceptions.FlinkApiCommonException;
import org.springframework.http.HttpStatus;

/**
 * <p>
 * description
 * </p>
 *
 * @author abigballofmud 2019/11/22 11:49
 * @since 1.0
 */
public final class Preconditions {

    private Preconditions() {
        throw new IllegalStateException("util class!");
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (Objects.isNull(reference)) {
            throw new FlinkApiCommonException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        } else {
            return reference;
        }
    }

    public static <T> void checkNull(T reference, String errorMessage) {
        if (Objects.nonNull(reference)) {
            throw new FlinkApiCommonException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        }
    }

    @SafeVarargs
    public static <T> boolean checkAllNotNull(T... reference) {
        return Stream.of(reference).noneMatch(Objects::isNull);
    }

    @SafeVarargs
    public static <T> boolean checkAllNull(T... reference) {
        return Stream.of(reference).allMatch(Objects::isNull);
    }

}
