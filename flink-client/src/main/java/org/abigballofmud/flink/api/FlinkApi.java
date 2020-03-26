package org.abigballofmud.flink.api;

import java.io.File;

import org.abigballofmud.flink.api.jars.FlinkJarService;
import org.abigballofmud.flink.domain.jars.JarUploadResponseBody;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 21:55
 * @since 1.0
 */
@Component
public class FlinkApi {

    private ApiClient apiClient;
    /**
     * flink jar 相关 api
     */
    private final FlinkJarService flinkJarService;

    public FlinkApi(@Qualifier("flinkRestTemplate") RestTemplate restTemplate) {
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

    public JarUploadResponseBody uploadJar(File file) {
        return flinkJarService.uploadJar(file, apiClient);
    }


}
