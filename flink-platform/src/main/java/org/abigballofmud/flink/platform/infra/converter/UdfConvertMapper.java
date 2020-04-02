package org.abigballofmud.flink.platform.infra.converter;

import org.abigballofmud.flink.platform.api.dto.UdfDTO;
import org.abigballofmud.flink.platform.domain.entity.Udf;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/31 12:12
 * @since 1.0
 */
@Mapper
public interface UdfConvertMapper {

    UdfConvertMapper INSTANCE = Mappers.getMapper(UdfConvertMapper.class);

    /**
     * entityToDTO
     *
     * @param udf Udf
     * @return org.abigballofmud.flink.platform.api.dto.UdfDTO
     */
    UdfDTO entityToDTO(Udf udf);

    /**
     * dtoToEntity
     *
     * @param udfDTO UdfDTO
     * @return org.abigballofmud.flink.platform.domain.entity.Udf
     */
    Udf dtoToEntity(UdfDTO udfDTO);

}
