package org.abigballofmud.flink.api.exceptions;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 21:10
 * @since 1.0
 */
public class FlinkApiCommonException extends RuntimeException {
    private static final long serialVersionUID = -7003755589740016882L;

    private final Integer code;

    public FlinkApiCommonException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public FlinkApiCommonException(Integer code, String msg, Throwable throwable) {
        super(msg, throwable);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
