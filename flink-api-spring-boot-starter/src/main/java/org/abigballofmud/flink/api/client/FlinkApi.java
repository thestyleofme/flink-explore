package org.abigballofmud.flink.api.client;

import java.io.File;

import org.abigballofmud.flink.api.client.jars.FlinkJarService;
import org.abigballofmud.flink.api.domain.jars.JarRunRequest;
import org.abigballofmud.flink.api.domain.jars.JarRunResponseBody;
import org.abigballofmud.flink.api.domain.jars.JarUploadResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 21:55
 * @since 1.0
 */
public class FlinkApi {

    private ApiClient apiClient;
    /**
     * flink jar 相关 api
     */
    private final FlinkJarService flinkJarService;

    public FlinkApi(RestTemplate restTemplate) {
        this.apiClient = new ApiClient();
        flinkJarService = new FlinkJarService(restTemplate);
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    //===================flink jar api=========================

    /**
     * upload flink jar
     *
     * @param file flink jar file
     * @return org.abigballofmud.flink.api.domain.jars.JarUploadResponseBody
     */
    public JarUploadResponseBody uploadJar(File file) {
        return flinkJarService.uploadJar(file, apiClient);
    }

    /**
     * run flink jar
     *
     * @param jarRunRequest JarRunRequest
     * @return org.abigballofmud.flink.api.domain.jars.JarRunResponseBody
     */
    public JarRunResponseBody runJar(JarRunRequest jarRunRequest) {
        return flinkJarService.runJar(jarRunRequest, apiClient);
    }

}
