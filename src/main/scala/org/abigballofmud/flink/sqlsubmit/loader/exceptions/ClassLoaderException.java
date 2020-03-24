package org.abigballofmud.flink.sqlsubmit.loader.exceptions;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 11:05
 * @since 1.0
 */
public class ClassLoaderException extends RuntimeException {

    public ClassLoaderException(String message) {
        super(message);
    }

    public ClassLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
