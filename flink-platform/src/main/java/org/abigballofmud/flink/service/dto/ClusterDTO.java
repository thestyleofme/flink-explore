package org.abigballofmud.flink.service.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.abigballofmud.flink.domain.enumerations.ClusterType;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:51
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterDTO implements Serializable {

    private static final long serialVersionUID = 854464206375410197L;

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private ClusterType type;

    /**
     * yaml格式
     * address: hdsp003
     * port: 50010
     */
    private String config;

    @NotNull
    private String remark;

    @NotNull
    private Long businessId;
}
