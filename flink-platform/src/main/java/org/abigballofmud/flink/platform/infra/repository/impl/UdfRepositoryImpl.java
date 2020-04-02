package org.abigballofmud.flink.platform.infra.repository.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.UdfDTO;
import org.abigballofmud.flink.platform.domain.entity.Udf;
import org.abigballofmud.flink.platform.domain.repository.UdfRepository;
import org.abigballofmud.flink.platform.infra.converter.UdfConvertMapper;
import org.abigballofmud.flink.platform.infra.mapper.UdfMapper;
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
public class UdfRepositoryImpl implements UdfRepository {

    private final UdfMapper udfMapper;

    public UdfRepositoryImpl(UdfMapper udfMapper) {
        this.udfMapper = udfMapper;
    }

    @Override
    public IPage<UdfDTO> pageAndSortDTO(UdfDTO udfDTO, Page<Udf> udfPage) {
        final QueryWrapper<Udf> queryWrapper = this.commonQueryWrapper(udfDTO);
        Page<Udf> entityPage = udfMapper.selectPage(udfPage, queryWrapper);
        final Page<UdfDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(entityPage, dtoPage);
        dtoPage.setRecords(entityPage.getRecords().stream()
                .map(UdfConvertMapper.INSTANCE::entityToDTO)
                .collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public UdfDTO detail(Long tenantId, Long udfId) {
        Udf udf = udfMapper.selectOne(detailWrapper(tenantId, udfId));
        return UdfConvertMapper.INSTANCE.entityToDTO(udf);
    }

    private Wrapper<Udf> detailWrapper(Long tenantId, Long udfId) {
        final QueryWrapper<Udf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Udf.FIELD_TENANT_ID, tenantId);
        queryWrapper.eq(Udf.FIELD_UDF_ID, udfId);
        return queryWrapper;
    }

    private QueryWrapper<Udf> commonQueryWrapper(UdfDTO udfDTO) {
        final QueryWrapper<Udf> queryWrapper = new QueryWrapper<>();
        Optional.ofNullable(udfDTO.getUdfId())
                .ifPresent(s -> queryWrapper.or().eq(Udf.FIELD_UDF_ID, s));
        Optional.ofNullable(udfDTO.getUdfName())
                .ifPresent(s -> queryWrapper.or().like(Udf.FIELD_UDF_NAME, s));
        Optional.ofNullable(udfDTO.getUdfType())
                .ifPresent(s -> queryWrapper.or().eq(Udf.FIELD_UDF_TYPE, s));
        Optional.ofNullable(udfDTO.getClusterCode())
                .ifPresent(s -> queryWrapper.or().eq(Udf.FIELD_CLUSTER_CODE, s));
        queryWrapper.eq(Udf.FIELD_TENANT_ID, udfDTO.getTenantId());
        return queryWrapper;
    }
}
