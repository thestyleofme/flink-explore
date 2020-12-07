package com.github.thestyleofme.flink.practice.sqlsubmit.loader.exceptions;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 16:07
 * @since 1.0
 */
public class GroovyCompileException extends RuntimeException {

    public GroovyCompileException(String message) {
        super(message);
    }

    public GroovyCompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
