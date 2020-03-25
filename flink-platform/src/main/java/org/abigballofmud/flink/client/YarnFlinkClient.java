package org.abigballofmud.flink.client;

import java.util.function.Consumer;

import org.abigballofmud.flink.client.request.SubmitRequest;
import org.abigballofmud.flink.client.response.SubmitFlinkResponse;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 21:40
 * @since 1.0
 */
public class YarnFlinkClient implements FlinkClient {

    @Override
    public void submit(SubmitRequest request, Consumer<SubmitFlinkResponse> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWebInterfaceUrl() {
        throw new UnsupportedOperationException();
    }
}
