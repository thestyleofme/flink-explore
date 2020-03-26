package org.abigballofmud.flink.api.jars;

import java.io.File;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.api.ApiClient;
import org.abigballofmud.flink.api.FlinkCluster;
import org.abigballofmud.flink.constants.FlinkApiConstant;
import org.abigballofmud.flink.domain.jars.JarUploadResponseBody;
import org.abigballofmud.flink.execeptions.CommonException;
import org.abigballofmud.flink.utils.RestTemplateUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
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

    public JarUploadResponseBody uploadJar(File file, ApiClient apiClient) {
        // 校验apiClient中的flinkCluster
        FlinkCluster flinkCluster = apiClient.getFlinkCluster();
        Assert.isTrue(checkApiClient(apiClient), "Please check the flink jobManagerUrl and uploadJarPath are configured");
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
            throw new CommonException("error.flink.jar.upload.status");
        }
    }

    private boolean checkApiClient(ApiClient apiClient) {
        FlinkCluster flinkCluster = apiClient.getFlinkCluster();
        return !Objects.isNull(flinkCluster) &&
                !StringUtils.isEmpty(flinkCluster.getJobManagerUrl()) &&
                !StringUtils.isEmpty(flinkCluster.getUploadJarPath());
    }
}
