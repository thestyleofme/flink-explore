package org.abigballofmud.flink.api.client;

import java.util.Set;

import javax.validation.constraints.NotBlank;

import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 22:17
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FlinkCluster {

    @NotBlank
    private String clusterCode;
    /**
     * jobManager地址
     */
    @NotBlank
    private String jobManagerUrl;
    /**
     * 若配置了Ha，这里是备用的jm
     */
    private Set<String> jobManagerStandbyUrlSet;

}
