package org.abigballofmud.flink.platform.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.SqlJobDTO;
import org.abigballofmud.flink.platform.domain.entity.SqlJob;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 9:39
 * @since 1.0
 */
public interface SqlJobRepository {

    /**
     * 根据条件分页查询flink sql job
     *
     * @param sqlJobDTO  SqlJobDTO
     * @param sqlJobPage Page<SqlJob>
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.abigballofmud.flink.platform.api.dto.SqlJobDTO>
     */
    IPage<SqlJobDTO> pageAndSortDTO(SqlJobDTO sqlJobDTO, Page<SqlJob> sqlJobPage);

    /**
     * 详细查询flink sql job
     *
     * @param tenantId 租户id
     * @param jobId    job_id
     * @return org.abigballofmud.flink.platform.api.dto.SqlJobDTO
     */
    SqlJobDTO detail(Long tenantId, Long jobId);
}
