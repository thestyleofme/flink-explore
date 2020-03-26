package org.abigballofmud.flink.api;

import java.util.function.Consumer;

import org.abigballofmud.flink.api.request.SubmitRequest;
import org.abigballofmud.flink.api.response.SubmitFlinkResponse;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:07
 * @since 1.0
 */
public interface FlinkClient {

    /**
     * 提交flink job
     *
     * @param request  SubmitRequest
     * @param consumer Consumer<SubmitFlinkResponse>
     */
    void submit(SubmitRequest request, Consumer<SubmitFlinkResponse> consumer);

    /**
     * 获取flink jobManager url
     * @return flink jobManager url
     */
    String getWebInterfaceUrl();

}
