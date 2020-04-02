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
@TableName(value = "flink_udf")
public class Udf implements Serializable {

    private static final long serialVersionUID = 854464206375410197L;

    public static final String FIELD_UDF_ID = "udf_id";
    public static final String FIELD_UDF_NAME = "udf_name";
    public static final String FIELD_UDF_TYPE = "udf_type";
    public static final String FIELD_TENANT_ID = "tenant_id";
    public static final String FIELD_CLUSTER_CODE = "cluster_code";
    public static final String FIELD_UDF_STATUS = "udf_status";

    @TableId(type = IdType.AUTO)
    private Long udfId;

    @NotBlank
    private String udfName;

    private String udfDesc;
    /**
     * jar/code
     */
    @NotBlank
    private String udfType;

    /**
     * udf jar上传路径
     */
    private String udfJarPath;
    /**
     * code: udf代码
     * jar: class
     */
    private String content;
    private String clusterCode;
    private String udfStatus;

    private Long tenantId;
    @Version
    private Long objectVersionNumber;
    private LocalDateTime creationDate;
    private Long createdBy;
    private LocalDateTime lastUpdateDate;
    private Long lastUpdatedBy;

}
