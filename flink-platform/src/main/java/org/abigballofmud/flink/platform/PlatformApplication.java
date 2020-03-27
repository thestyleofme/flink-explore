package org.abigballofmud.flink.platform;

import java.io.File;
import java.util.HashSet;

import org.abigballofmud.flink.api.client.FlinkApi;
import org.abigballofmud.flink.api.client.FlinkCluster;
import org.abigballofmud.flink.api.domain.jars.JarRunRequest;
import org.abigballofmud.flink.api.domain.jars.JarRunResponseBody;
import org.abigballofmud.flink.api.domain.jars.JarUploadResponseBody;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
            ConfigurableApplicationContext context = SpringApplication.run(PlatformApplication.class, args);
            // 上传jar
            FlinkApi flinkApi = context.getBean(FlinkApi.class);
            HashSet<String> hashSet = new HashSet<>();
            hashSet.add("http://hdsp002:50012");
            flinkApi.getApiClient().setFlinkCluster(FlinkCluster.builder()
                    .clusterId(1L)
                    .jobManagerUrl("http://hdsp003:50100")
                    .jobManagerStandbyUrlSet(hashSet)
                    .uploadJarPath("/data/flink/upload_jars")
                    .build());
            JarUploadResponseBody responseBody =
                    flinkApi.uploadJar(new File("C:\\Users\\isacc\\Desktop\\WordCount.jar"));
            System.out.println(responseBody);
            // 运行jar
            String filename = responseBody.getFilename();
            JarRunResponseBody jarRunResponseBody = flinkApi.runJar(JarRunRequest.builder()
                    .jarId(filename.substring(filename.lastIndexOf('/') + 1))
                    .entryClass("org.apache.flink.streaming.examples.wordcount.WordCount")
                    .parallelism(1)
                    .build());
            System.out.println(jarRunResponseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
