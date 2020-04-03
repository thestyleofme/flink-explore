package org.abigballofmud.flink.platform.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.abigballofmud.flink.platform.api.dto.UdfDTO;
import org.abigballofmud.flink.platform.domain.entity.Udf;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 11:45
 * @since 1.0
 */
public interface UdfService extends IService<Udf> {

    /**
     * 插入并注册flink udf
     *
     * @param udfDTO UdfDTO
     * @param multipartFile MultipartFile
     * @return org.abigballofmud.flink.platform.api.dto.UdfDTO
     */
    UdfDTO insert(UdfDTO udfDTO, MultipartFile multipartFile);
}
