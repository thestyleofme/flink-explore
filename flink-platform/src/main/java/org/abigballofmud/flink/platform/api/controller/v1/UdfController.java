package org.abigballofmud.flink.platform.api.controller.v1;

import javax.validation.Valid;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.UdfDTO;
import org.abigballofmud.flink.platform.app.service.UdfService;
import org.abigballofmud.flink.platform.domain.entity.Udf;
import org.abigballofmud.flink.platform.domain.repository.UdfRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 11:17
 * @since 1.0
 */
@RestController("udfController.v1")
@RequestMapping("/v1/{tenantId}/udf")
public class UdfController {

    private final UdfRepository udfRepository;
    private final UdfService udfService;

    public UdfController(UdfRepository udfRepository,
                         UdfService udfService) {
        this.udfRepository = udfRepository;
        this.udfService = udfService;
    }

    @GetMapping
    public IPage<UdfDTO> list(@PathVariable Long tenantId,
                              UdfDTO udfDTO,
                              Page<Udf> udfPage) {
        udfDTO.setTenantId(tenantId);
        udfPage.addOrder(OrderItem.desc(Udf.FIELD_UDF_ID));
        return udfRepository.pageAndSortDTO(udfDTO, udfPage);
    }

    @GetMapping("/{udfId}")
    public UdfDTO detail(@PathVariable Long tenantId,
                         @PathVariable Long udfId) {
        return udfRepository.detail(tenantId, udfId);
    }

    @PostMapping
    public UdfDTO insert(@PathVariable Long tenantId,
                         @RequestPart(value = "udfDTO") @Valid UdfDTO udfDTO,
                         @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        udfDTO.setTenantId(tenantId);
        return udfService.insert(udfDTO, multipartFile);
    }

}
