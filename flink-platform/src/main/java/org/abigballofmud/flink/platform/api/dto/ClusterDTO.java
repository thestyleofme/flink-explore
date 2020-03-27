package org.abigballofmud.flink.platform.api.dto;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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

    private Long clusterId;

    @NotBlank
    private String clusterCode;

    private String clusterDesc;

    private String jobManagerUrl;

    /**
     * 若配置了Ha，这里是备用的jm
     */
    private List<String> jobManagerStandbyUrlSet;

}
