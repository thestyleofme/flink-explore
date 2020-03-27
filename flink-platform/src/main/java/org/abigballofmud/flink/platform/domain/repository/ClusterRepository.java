package org.abigballofmud.flink.platform.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.ClusterDTO;
import org.abigballofmud.flink.platform.domain.entity.Cluster;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/28 2:20
 * @since 1.0
 */
public interface ClusterRepository {

    /**
     * 分页条件查询flink集群
     *
     * @param clusterDTO  ClusterDTO
     * @param clusterPage Page<Cluster>
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.abigballofmud.flink.platform.api.dto.ClusterDTO>
     */
    IPage<ClusterDTO> pageAndSortDTO(ClusterDTO clusterDTO, Page<Cluster> clusterPage);
}
