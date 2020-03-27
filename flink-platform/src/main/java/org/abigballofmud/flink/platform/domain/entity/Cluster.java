package org.abigballofmud.flink.platform.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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
@TableName(value = "cluster")
public class Cluster implements Serializable {

    private static final long serialVersionUID = 854464206375410197L;

    public static final String FIELD_CLUSTER_ID = "cluster_id";

    @TableId(type = IdType.AUTO)
    private Long clusterId;

    @NotBlank
    private String clusterCode;

    private String clusterDesc;
    @NotBlank
    private String jobManagerUrl;

    /**
     * 若配置了Ha，这里是备用的jm，逗号分割
     */
    private String jobManagerStandbyUrl;

    private Integer enabledFlag;

    private Long tenantId;
    @Version
    private Long objectVersionNumber;
    private LocalDateTime creationDate;
    private Long createdBy;
    private LocalDateTime lastUpdateDate;
    private Long lastUpdatedBy;

}
