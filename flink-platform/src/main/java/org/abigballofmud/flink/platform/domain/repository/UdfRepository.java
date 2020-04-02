package org.abigballofmud.flink.platform.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.UdfDTO;
import org.abigballofmud.flink.platform.domain.entity.Udf;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 9:39
 * @since 1.0
 */
public interface UdfRepository {

    /**
     * 分页条件查询flink udf
     *
     * @param udfDTO  UdfDTO
     * @param udfPage Page<Udf>
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.abigballofmud.flink.platform.api.dto.UdfDTO>
     */
    IPage<UdfDTO> pageAndSortDTO(UdfDTO udfDTO, Page<Udf> udfPage);

    /**
     * udf详细信息
     *
     * @param tenantId 租户id
     * @param udfId    udfId
     * @return org.abigballofmud.flink.platform.api.dto.UdfDTO
     */
    UdfDTO detail(Long tenantId, Long udfId);

}
