package org.abigballofmud.flink.api.domain;

import java.io.Serializable;

/**
 * <p>
 * description
 * </p>
 *
 * @author abigballofmud 2019/11/21 14:36
 * @since 1.0
 */
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = -8365417054147857986L;

    private Boolean failed;

    /**
     * "type": "warn"
     */
    private String type;

    /**
     * -1，代表全局异常处理的错误
     * 0，成功
     * 正数，业务错误码
     */
    private Integer code;

    /**
     * 描述
     */
    private String message;

    /**
     * 数据
     */
    private transient T data;

    private ApiResult() {
    }

    private ApiResult(Boolean failed, Integer code, String message, T data, String type) {
        this.failed = failed;
        this.code = code;
        this.message = message;
        this.data = data;
        this.type = type;
    }

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  类型
     * @return Api
     */
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(false, 0, null, data, null);
    }

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  类型
     * @return Api
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(false, 200, null, data, null);
    }

    /**
     * 失败
     *
     * @param code    业务错误码
     * @param message 信息
     * @return ApiResult
     */
    public static ApiResult<Void> fail(Integer code, String message) {
        return new ApiResult<>(true, code, message, null, "error");
    }

    /**
     * 失败
     *
     * @param <T>     T
     * @param code    业务错误码
     * @param message 信息
     * @return ApiResult
     */
    public static <T> ApiResult<T> fail(T data, Integer code, String message) {
        return new ApiResult<>(true, code, message, data, "error");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
