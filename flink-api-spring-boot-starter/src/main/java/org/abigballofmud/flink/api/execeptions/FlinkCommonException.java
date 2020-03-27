package org.abigballofmud.flink.api.execeptions;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/27 16:31
 * @since 1.0
 */
public class FlinkCommonException extends RuntimeException {

    private static final long serialVersionUID = -6167077238093684100L;

    public FlinkCommonException(String msg) {
        super(msg);
    }

    public FlinkCommonException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
