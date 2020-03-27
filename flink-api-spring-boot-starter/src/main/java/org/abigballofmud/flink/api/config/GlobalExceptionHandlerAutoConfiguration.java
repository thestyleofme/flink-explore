package org.abigballofmud.flink.api.config;

import org.abigballofmud.flink.api.execeptions.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 全局异常处理配置
 * </p>
 *
 * @author abigballofmud 2019/11/21 14:51
 * @since 1.0
 */
@Configuration
public class GlobalExceptionHandlerAutoConfiguration {

    @Bean("flinkGlobalExceptionHandler")
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

}
