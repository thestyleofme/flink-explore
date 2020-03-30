package org.abigballofmud.flink.api.utils;

import org.abigballofmud.flink.api.client.ApiClient;
import org.abigballofmud.flink.api.client.FlinkCluster;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/28 0:58
 * @since 1.0
 */
public class FlinkApiUtil {

    private FlinkApiUtil() {
        throw new IllegalStateException("util class");
    }

    /**
     * 校验ApiClient中的FlinkCluster是否设置
     * clusterCode以及jobManagerUrl不能为空
     *
     * @param apiClient ApiClient
     * @return boolean 是否设置
     */
    public boolean checkApiClient(ApiClient apiClient) {
        FlinkCluster flinkCluster = apiClient.getFlinkCluster();
        return Preconditions.checkAllNotNull(flinkCluster,
                flinkCluster.getClusterCode(),
                flinkCluster.getJobManagerUrl());
    }

    public void doApi() {

    }

}
