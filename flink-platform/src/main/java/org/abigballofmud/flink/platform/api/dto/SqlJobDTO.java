package org.abigballofmud.flink.platform.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class SqlJobDTO implements Serializable {

    private static final long serialVersionUID = 2962953322349253698L;

    public static final String FIELD_UDF_ID = "job_id";

    private Long jobId;
    @NotBlank
    private String jobCode;
    /**
     * jm master所在节点的sql上传路径
     */
    @NotBlank
    private String sqlUploadPath;
    private String savepointPath;
    /**
     * 运行后flink返回的jobid
     */
    private String flinkJobId;
    /**
     * sql任务的一些额外配置，如：
     * parallelism : 1
     * allowNonRestoredState : true
     * entryClass: ""
     */
    private String settingInfo;
    private String errors;

    @NotBlank
    private String clusterCode;
    /**
     * sql内容
     */
    @NotBlank
    private String content;

    private String jobStatus;

    private Long tenantId;
    private Long objectVersionNumber;
    private LocalDateTime creationDate;
    private Long createdBy;
    private LocalDateTime lastUpdateDate;
    private Long lastUpdatedBy;

    //===========other===========

    /**
     * 这里执行flink sql任务需要选取flink_upload_jar里面的jar去执行
     * flink_upload_jar里面jar的cluster_code以及租户tenant_id必须与sql_job一致
     * 若是没有传uploadJarId，就去flink_upload_jar找jarCode为_flink_sql_platform的最新的version去执行
     */
    private Long uploadJarId;

}
