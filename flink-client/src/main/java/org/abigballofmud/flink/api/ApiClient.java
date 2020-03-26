package org.abigballofmud.flink.api;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 23:11
 * @since 1.0
 */
public class ApiClient {

    private FlinkCluster flinkCluster;

    public FlinkCluster getFlinkCluster() {
        return flinkCluster;
    }

    public void setFlinkCluster(FlinkCluster flinkCluster) {
        this.flinkCluster = flinkCluster;
    }

}
