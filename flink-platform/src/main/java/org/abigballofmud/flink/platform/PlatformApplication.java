package org.abigballofmud.flink.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 11:40
 * @since 1.0
 */
@SpringBootApplication
@MapperScan("org.abigballofmud.flink.platform.infra.mapper")
public class PlatformApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(PlatformApplication.class, args);
        } catch (Exception e) {
            // 防止错误不打印
            e.printStackTrace();
        }
    }
}
