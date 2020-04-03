package org.abigballofmud.flink.platform.infra.converter;

import org.abigballofmud.flink.platform.api.dto.SqlJobDTO;
import org.abigballofmud.flink.platform.domain.entity.SqlJob;
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
public interface SqlJobConvertMapper {

    SqlJobConvertMapper INSTANCE = Mappers.getMapper(SqlJobConvertMapper.class);

    /**
     * entityToDTO
     *
     * @param sqlJob SqlJob
     * @return org.abigballofmud.flink.platform.api.dto.SqlJobDTO
     */
    SqlJobDTO entityToDTO(SqlJob sqlJob);

    /**
     * dtoToEntity
     *
     * @param sqlJobDTO SqlJobDTO
     * @return org.abigballofmud.flink.platform.domain.entity.SqlJob
     */
    SqlJob dtoToEntity(SqlJobDTO sqlJobDTO);

}
