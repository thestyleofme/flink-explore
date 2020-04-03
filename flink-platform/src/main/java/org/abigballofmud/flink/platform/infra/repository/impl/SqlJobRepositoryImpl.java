package org.abigballofmud.flink.platform.infra.repository.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.SqlJobDTO;
import org.abigballofmud.flink.platform.domain.entity.SqlJob;
import org.abigballofmud.flink.platform.domain.repository.SqlJobRepository;
import org.abigballofmud.flink.platform.infra.converter.SqlJobConvertMapper;
import org.abigballofmud.flink.platform.infra.mapper.SqlJobMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 9:43
 * @since 1.0
 */
@Component
public class SqlJobRepositoryImpl implements SqlJobRepository {

    private final SqlJobMapper sqlJobMapper;

    public SqlJobRepositoryImpl(SqlJobMapper sqlJobMapper) {
        this.sqlJobMapper = sqlJobMapper;
    }

    @Override
    public IPage<SqlJobDTO> pageAndSortDTO(SqlJobDTO sqlJobDTO, Page<SqlJob> sqlJobPage) {
        final QueryWrapper<SqlJob> queryWrapper = this.commonQueryWrapper(sqlJobDTO);
        Page<SqlJob> entityPage = sqlJobMapper.selectPage(sqlJobPage, queryWrapper);
        final Page<SqlJobDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(entityPage, dtoPage);
        dtoPage.setRecords(entityPage.getRecords().stream()
                .map(SqlJobConvertMapper.INSTANCE::entityToDTO)
                .collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public SqlJobDTO detail(Long tenantId, Long jobId) {
        SqlJob sqlJob = sqlJobMapper.selectOne(detailWrapper(tenantId, jobId));
        return SqlJobConvertMapper.INSTANCE.entityToDTO(sqlJob);
    }

    private Wrapper<SqlJob> detailWrapper(Long tenantId, Long jobId) {
        final QueryWrapper<SqlJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlJob.FIELD_TENANT_ID, tenantId);
        queryWrapper.eq(SqlJob.FIELD_JOB_ID, jobId);
        return queryWrapper;
    }

    private QueryWrapper<SqlJob> commonQueryWrapper(SqlJobDTO sqlJobDTO) {
        final QueryWrapper<SqlJob> queryWrapper = new QueryWrapper<>();
        Optional.ofNullable(sqlJobDTO.getJobId())
                .ifPresent(s -> queryWrapper.or().eq(SqlJob.FIELD_JOB_ID, s));
        Optional.ofNullable(sqlJobDTO.getJobCode())
                .ifPresent(s -> queryWrapper.or().eq(SqlJob.FIELD_JOB_CODE, s));
        Optional.ofNullable(sqlJobDTO.getClusterCode())
                .ifPresent(s -> queryWrapper.or().eq(SqlJob.FIELD_CLUSTER_CODE, s));
        queryWrapper.eq(SqlJob.FIELD_TENANT_ID, sqlJobDTO.getTenantId());
        return queryWrapper;
    }


}
