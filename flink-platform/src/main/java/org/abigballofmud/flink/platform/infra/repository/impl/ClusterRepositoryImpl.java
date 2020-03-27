package org.abigballofmud.flink.platform.infra.repository.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.ClusterDTO;
import org.abigballofmud.flink.platform.domain.entity.Cluster;
import org.abigballofmud.flink.platform.domain.repository.ClusterRepository;
import org.abigballofmud.flink.platform.infra.mapper.ClusterMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/28 2:24
 * @since 1.0
 */
@Component
public class ClusterRepositoryImpl implements ClusterRepository {

    private final ClusterMapper clusterMapper;

    public ClusterRepositoryImpl(ClusterMapper clusterMapper) {
        this.clusterMapper = clusterMapper;
    }

    @Override
    public IPage<ClusterDTO> pageAndSortDTO(ClusterDTO clusterDTO, Page<Cluster> clusterPage) {
        final QueryWrapper<Cluster> queryWrapper = this.commonQueryWrapper(clusterDTO);
        Page<Cluster> entityPage = clusterMapper.selectPage(clusterPage, queryWrapper);
        final Page<ClusterDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(entityPage, dtoPage);
        dtoPage.setRecords(entityPage.getRecords().stream()
                .map(cluster -> {
                    ClusterDTO build = ClusterDTO.builder().build();
                    BeanUtils.copyProperties(cluster, build);
                    return build;
                })
                .collect(Collectors.toList()));
        return dtoPage;
    }

    private QueryWrapper<Cluster> commonQueryWrapper(ClusterDTO clusterDTO) {
        final QueryWrapper<Cluster> queryWrapper = new QueryWrapper<>();
        Optional.ofNullable(clusterDTO.getClusterCode())
                .ifPresent(s -> queryWrapper.or().like("cluster_code", s));
        Optional.ofNullable(clusterDTO.getJobManagerUrl())
                .ifPresent(s -> queryWrapper.or().like("job_manager_url", s));
        queryWrapper.eq("tenant_id", clusterDTO.getTenantId());
        return queryWrapper;
    }
}
