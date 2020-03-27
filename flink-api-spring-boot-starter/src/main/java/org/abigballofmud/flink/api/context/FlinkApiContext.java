package org.abigballofmud.flink.api.context;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.abigballofmud.flink.api.client.FlinkApi;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/28 0:37
 * @since 1.0
 */
public class FlinkApiContext {

    private final Map<String, FlinkApi> flinkApiMap = new ConcurrentHashMap<>(16);

    private final RestTemplate flinkRestTemplate;

    public FlinkApiContext(RestTemplate flinkRestTemplate) {
        this.flinkRestTemplate = flinkRestTemplate;
    }

    public FlinkApi get(String clusterCode) {
        if (Objects.isNull(flinkApiMap.get(clusterCode))) {
            FlinkApi flinkApi = new FlinkApi(flinkRestTemplate);
            // 为了防止cluster修改，需手动setApiClient
            flinkApiMap.put(clusterCode, flinkApi);
            return flinkApi;
        }
        return flinkApiMap.get(clusterCode);
    }

}
