package org.abigballofmud.flink.api;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.api.loader.JarLoader;
import org.abigballofmud.flink.api.request.JarSubmitFlinkRequest;
import org.abigballofmud.flink.api.request.SubmitRequest;
import org.abigballofmud.flink.api.response.SubmitFlinkResponse;
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

    public StandaloneClusterFlinkClient(T clusterClient,
                                        JarLoader jarLoader,
                                        String webInterfaceUrl) {
        super(jarLoader);
        this.clusterClient = clusterClient;
        this.webInterfaceUrl = webInterfaceUrl;
    }

    @Override
    public void submit(SubmitRequest request, Consumer<SubmitFlinkResponse> consumer) {
        log.debug("submit");
        if (request instanceof JarSubmitFlinkRequest) {
            submitJar(clusterClient, (JarSubmitFlinkRequest) request, consumer);
        } else {
            throw new UnsupportedOperationException();
        }
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
