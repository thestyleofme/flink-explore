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

}
