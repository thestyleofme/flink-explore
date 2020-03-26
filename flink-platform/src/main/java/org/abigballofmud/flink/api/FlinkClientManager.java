package org.abigballofmud.flink.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.abigballofmud.flink.api.loader.JarLoader;
import org.abigballofmud.flink.service.dto.ClusterDTO;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 21:33
 * @since 1.0
 */
@Component
public class FlinkClientManager {

    private final JarLoader jarLoader;

    private final Map<Long, FlinkClient> clusterClients = new ConcurrentHashMap<>();

    public FlinkClientManager(JarLoader jarLoader) {
        this.jarLoader = jarLoader;
    }

    public FlinkClient getClient(Long clusterId) {
        return clusterClients.get(clusterId);
    }

    public void addClientOnly(ClusterDTO cluster) throws JsonProcessingException {
        FlinkClient client = ClusterClientFactory.get(cluster,jarLoader);
        clusterClients.put(cluster.getId(), client);
    }

    public void putClient(ClusterDTO cluster) throws JsonProcessingException {
        FlinkClient client = ClusterClientFactory.get(cluster,jarLoader);
        clusterClients.put(cluster.getId(), client);
    }

    public void updateClient(ClusterDTO cluster) throws JsonProcessingException {
        FlinkClient client = ClusterClientFactory.get(cluster,jarLoader);
        clusterClients.put(cluster.getId(), client);
    }

    public void deleteClient(ClusterDTO cluster) {
        clusterClients.remove(cluster.getId());
    }

}
