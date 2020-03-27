package org.abigballofmud.flink.api.execeptions;

import org.abigballofmud.flink.api.domain.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>
 * description
 * </p>
 *
 * @author abigballofmud 2019/11/21 14:49
 * @since 1.0
 */
@Order(100)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FlinkApiCommonException.class)
    public ApiResult<Void> handleFlinkApiCommonException(FlinkApiCommonException e) {
        LOG.warn("Handle FlinkApiCommonException", e);
        Integer code = e.getCode();
        String msg = e.getMessage();
        return ApiResult.fail(code, msg);
    }

    @ExceptionHandler(FlinkCommonException.class)
    public ApiResult<Void> handleFlinkCommonException(FlinkCommonException e) {
        LOG.warn("Handle FlinkCommonException", e);
        String msg = e.getMessage();
        return ApiResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e) {
        LOG.warn("Handle Exception", e);
        String msg = e.getMessage();
        return ApiResult.fail(-1, msg);
    }
}
