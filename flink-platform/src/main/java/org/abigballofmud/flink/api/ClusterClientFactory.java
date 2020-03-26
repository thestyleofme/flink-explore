package org.abigballofmud.flink.api;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.abigballofmud.flink.api.loader.JarLoader;
import org.abigballofmud.flink.domain.enumerations.ClusterType;
import org.abigballofmud.flink.execeptions.CommonException;
import org.abigballofmud.flink.service.dto.ClusterDTO;
import org.abigballofmud.flink.utils.BindPropertiesUtil;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:47
 * @since 1.0
 */
public class ClusterClientFactory {

    private ClusterClientFactory() {
        throw new IllegalStateException();
    }

    public static FlinkClient get(ClusterDTO cluster, JarLoader jarLoader) throws JsonProcessingException {
        ClusterType clusterType = cluster.getType();
        switch (clusterType) {
            case STANDALONE:
                StandaloneClusterInfo clusterInfo = BindPropertiesUtil.bindProperties(cluster.getConfig(), StandaloneClusterInfo.class);
                return createRestClient(clusterInfo, jarLoader);
            case YARN:
                // todo 支持yarn cluster
            default:
                throw new UnsupportedOperationException("only support rest client ");
        }
    }

    public static FlinkClient createRestClient(StandaloneClusterInfo clusterInfo, JarLoader jarLoader) {
        Configuration configuration = new Configuration();
        if (clusterInfo.getProperties() != null) {
            setProperties(clusterInfo.getProperties(), configuration);
        }
        configuration.setString(JobManagerOptions.ADDRESS, clusterInfo.getAddress());
        configuration.setInteger(JobManagerOptions.PORT, clusterInfo.getPort());
        configuration.setInteger(RestOptions.PORT, clusterInfo.getPort());
        return createClient(configuration, jarLoader, clusterInfo.getWebInterfaceUrl());
    }

    @SuppressWarnings("rawtypes")
    private static FlinkClient createClient(Configuration configuration, JarLoader jarLoader, String webUrl) {
        try (RestClusterClient<String> restClient = new RestClusterClient<>(configuration, "RemoteExecutor")) {
            return new StandaloneClusterFlinkClient<RestClusterClient>(restClient, jarLoader, webUrl);
        } catch (Exception e) {
            throw new CommonException("Cannot establish connection to JobManager: " + e.getMessage(), e);
        }
    }

    private static void setProperties(Map<String, Object> properties, Configuration configuration) {
        properties.forEach((key, value) -> {
            if (value instanceof String) {
                configuration.setString(key, value.toString());
            } else if (value instanceof Boolean) {
                configuration.setBoolean(key, (Boolean) value);
            } else if (value instanceof Long) {
                configuration.setLong(key, (Long) value);
            } else if (value instanceof Float) {
                configuration.setFloat(key, (Float) value);
            } else if (value instanceof Integer) {
                configuration.setInteger(key, (Integer) value);
            } else if (value instanceof Double) {
                configuration.setDouble(key, (Double) value);
            } else {
                configuration.setString(key, value.toString());
            }
        });
    }
}
