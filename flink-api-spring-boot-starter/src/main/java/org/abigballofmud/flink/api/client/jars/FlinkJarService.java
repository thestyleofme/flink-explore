package org.abigballofmud.flink.api.client.jars;

import java.io.File;
import java.util.Objects;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.api.client.ApiClient;
import org.abigballofmud.flink.api.client.FlinkCluster;
import org.abigballofmud.flink.api.constants.FlinkApiConstant;
import org.abigballofmud.flink.api.domain.jars.JarRunRequest;
import org.abigballofmud.flink.api.domain.jars.JarRunResponseBody;
import org.abigballofmud.flink.api.domain.jars.JarUploadResponseBody;
import org.abigballofmud.flink.api.execeptions.FlinkApiCommonException;
import org.abigballofmud.flink.api.utils.JSON;
import org.abigballofmud.flink.api.utils.RestTemplateUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 22:42
 * @since 1.0
 */
@Slf4j
public class FlinkJarService {

    private final RestTemplate restTemplate;

    public FlinkJarService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    public JarRunResponseBody runJar(JarRunRequest jarRunRequest, ApiClient apiClient) {
        HttpEntity<String> requestEntity =
                new HttpEntity<>((JSON.toJson(jarRunRequest)), RestTemplateUtil.applicationJsonHeaders());
        FlinkCluster flinkCluster = apiClient.getFlinkCluster();
        ResponseEntity<JarRunResponseBody> responseEntity =
                restTemplate.exchange(flinkCluster.getJobManagerUrl() +
                                String.format(FlinkApiConstant.Jars.RUN_JAR, jarRunRequest.getJarId()),
                        HttpMethod.POST, requestEntity, JarRunResponseBody.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JarRunResponseBody data = responseEntity.getBody();
            log.info("run jar result: {}", data);
            return data;
        } else {
            throw new FlinkApiCommonException(responseEntity.getStatusCode().value(),
                    String.format("error.flink.jar.run.status, %s", responseEntity.getBody()));
        }
    }


    /**
     * 上传flink jar
     *
     * @param file      jar file
     * @param apiClient ApiClient
     * @return org.abigballofmud.flink.api.domain.jars.JarUploadResponseBody
     */
    public JarUploadResponseBody uploadJar(File file, ApiClient apiClient) {
        // 校验apiClient中的flinkCluster
        Assert.isTrue(checkApiClient(apiClient), "Please check the flink jobManagerUrl and uploadJarPath are configured");
        FlinkCluster flinkCluster = apiClient.getFlinkCluster();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>(1);
        body.add("file", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, RestTemplateUtil.applicationMultiDataHeaders());
        ResponseEntity<JarUploadResponseBody> responseEntity =
                restTemplate.exchange(flinkCluster.getJobManagerUrl() + FlinkApiConstant.Jars.UPLOAD_JAR,
                        HttpMethod.POST, requestEntity, JarUploadResponseBody.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JarUploadResponseBody data = responseEntity.getBody();
            log.info("upload jar result: {}", data);
            return data;
        } else {
            throw new FlinkApiCommonException(responseEntity.getStatusCode().value(),
                    String.format("error.flink.jar.upload.status, %s", responseEntity.getBody()));
        }
    }

    private boolean checkApiClient(ApiClient apiClient) {
        FlinkCluster flinkCluster = apiClient.getFlinkCluster();
        return !Objects.isNull(flinkCluster) &&
                !StringUtils.isEmpty(flinkCluster.getJobManagerUrl());
    }
}
