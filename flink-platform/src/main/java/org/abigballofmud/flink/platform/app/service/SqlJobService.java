package org.abigballofmud.flink.platform.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.abigballofmud.flink.platform.api.dto.SqlJobDTO;
import org.abigballofmud.flink.platform.domain.entity.SqlJob;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 11:45
 * @since 1.0
 */
public interface SqlJobService extends IService<SqlJob> {

    /**
     * 创建flink sql job
     *
     * @param sqlJobDTO SqlJobDTO
     * @return org.abigballofmud.flink.platform.api.dto.SqlJobDTO
     */
    SqlJobDTO create(SqlJobDTO sqlJobDTO);
}
