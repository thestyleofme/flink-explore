package org.abigballofmud.flink.platform.app.service;

import org.abigballofmud.flink.platform.api.dto.UdfDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 11:45
 * @since 1.0
 */
public interface UdfService {

    /**
     * 插入并注册flink udf
     *
     * @param udfDTO UdfDTO
     * @param multipartFile MultipartFile
     * @return org.abigballofmud.flink.platform.api.dto.UdfDTO
     */
    UdfDTO insert(UdfDTO udfDTO, MultipartFile multipartFile);
}
