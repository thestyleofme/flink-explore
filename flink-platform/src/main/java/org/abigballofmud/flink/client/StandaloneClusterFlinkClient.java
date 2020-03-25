package org.abigballofmud.flink.client;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.client.request.SubmitRequest;
import org.abigballofmud.flink.client.response.SubmitFlinkResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.ClusterClient;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:36
 * @since 1.0
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class StandaloneClusterFlinkClient<T extends ClusterClient> extends AbstractFlinkClient {

    private final T clusterClient;

    private final String webInterfaceUrl;

    public StandaloneClusterFlinkClient(T clusterClient, String webInterfaceUrl) {
        this.clusterClient = clusterClient;
        this.webInterfaceUrl = webInterfaceUrl;
    }

    @Override
    public void submit(SubmitRequest request, Consumer<SubmitFlinkResponse> consumer) {
        log.debug("submit");
    }

    @Override
    public String getWebInterfaceUrl() {
        if (StringUtils.isEmpty(this.webInterfaceUrl)) {
            return clusterClient.getWebInterfaceURL();
        } else {
            return this.webInterfaceUrl;
        }
    }

}
