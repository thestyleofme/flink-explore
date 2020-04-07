package org.abigballofmud.flink.platform.api.controller.v1;

import javax.validation.Valid;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.SqlJobDTO;
import org.abigballofmud.flink.platform.app.service.SqlJobService;
import org.abigballofmud.flink.platform.domain.entity.SqlJob;
import org.abigballofmud.flink.platform.domain.repository.SqlJobRepository;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 11:17
 * @since 1.0
 */
@RestController("sqlJobController.v1")
@RequestMapping("/v1/{tenantId}/sql-job")
public class SqlJobController {

    private final SqlJobRepository sqlJobRepository;
    private final SqlJobService sqlJobService;

    public SqlJobController(SqlJobRepository sqlJobRepository,
                            SqlJobService sqlJobService) {
        this.sqlJobRepository = sqlJobRepository;
        this.sqlJobService = sqlJobService;
    }

    @GetMapping
    public IPage<SqlJobDTO> list(@PathVariable Long tenantId,
                                 SqlJobDTO sqlJobDTO,
                                 Page<SqlJob> sqlJobPage) {
        sqlJobDTO.setTenantId(tenantId);
        sqlJobPage.addOrder(OrderItem.desc(SqlJob.FIELD_JOB_ID));
        return sqlJobRepository.pageAndSortDTO(sqlJobDTO, sqlJobPage);
    }

    @GetMapping("/{jobId}")
    public SqlJobDTO detail(@PathVariable Long tenantId,
                            @PathVariable Long jobId) {
        return sqlJobRepository.detail(tenantId, jobId);
    }

    @PostMapping
    public SqlJobDTO create(@PathVariable Long tenantId,
                            @RequestBody @Valid SqlJobDTO sqlJobDTO) {
        sqlJobDTO.setTenantId(tenantId);
        return sqlJobService.create(sqlJobDTO);
    }

    @PutMapping
    public SqlJobDTO update(@PathVariable Long tenantId,
                            @RequestBody @Valid SqlJobDTO sqlJobDTO) {
        sqlJobDTO.setTenantId(tenantId);
        return sqlJobService.update(sqlJobDTO);
    }

    @GetMapping("/execute/{jobId}")
    public SqlJobDTO execute(@PathVariable Long tenantId,
                             @PathVariable Long jobId,
                             @RequestParam(required = false) Long uploadJarId) {
        return sqlJobService.execute(tenantId, jobId, uploadJarId);
    }

}
