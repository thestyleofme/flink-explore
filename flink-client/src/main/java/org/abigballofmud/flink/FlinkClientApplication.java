package org.abigballofmud.flink;

import java.io.File;
import java.util.HashSet;

import org.abigballofmud.flink.api.FlinkApi;
import org.abigballofmud.flink.api.FlinkCluster;
import org.abigballofmud.flink.domain.jars.JarUploadResponseBody;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 23:23
 * @since 1.0
 */
@SpringBootApplication
public class FlinkClientApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(FlinkClientApplication.class, args);
            FlinkApi flinkApi = context.getBean(FlinkApi.class);
            HashSet<String> hashSet = new HashSet<>();
            hashSet.add("http://hdsp002:50012");
            flinkApi.getApiClient().setFlinkCluster(FlinkCluster.builder()
                    .clusterId(1L)
                    .jobManagerUrl("http://hdsp003:50100")
                    .jobManagerStandbyUrlSet(hashSet)
                    .uploadJarPath("/data/flink/upload_jars/")
                    .build());
            JarUploadResponseBody responseBody =
                    flinkApi.uploadJar(new File("C:\\Users\\isacc\\Desktop\\WordCount.jar"));
            System.out.println(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
