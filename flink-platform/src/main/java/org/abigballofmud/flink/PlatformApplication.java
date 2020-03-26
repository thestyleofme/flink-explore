package org.abigballofmud.flink;

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
public class PlatformApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(PlatformApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
