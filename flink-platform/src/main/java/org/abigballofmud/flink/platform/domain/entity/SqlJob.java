package org.abigballofmud.flink.platform.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/4/3 10:32
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName(value = "flink_sql_job")
public class SqlJob implements Serializable {

    private static final long serialVersionUID = -5805555445072632397L;

    public static final String FIELD_JOB_ID = "job_id";
    public static final String FIELD_JOB_CODE = "job_code";
    public static final String FIELD_TENANT_ID = "tenant_id";
    public static final String FIELD_CLUSTER_CODE = "cluster_code";
    public static final String FIELD_JOB_STATUS = "job_status";

    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;
    private String jobCode;

    private String clusterCode;
    private String sqlUploadPath;
    private String savepointPath;
    private String flinkJobId;
    private String settingInfo;
    private String errors;
    /**
     * sql内容
     */
    private String content;

    /**
     * 执行sql的状态
     */
    private String jobStatus;


    private Long tenantId;
    @Version
    private Long objectVersionNumber;
    private LocalDateTime creationDate;
    private Integer createdBy;
    private Integer lastUpdatedBy;
    private LocalDateTime lastUpdateDate;


}
