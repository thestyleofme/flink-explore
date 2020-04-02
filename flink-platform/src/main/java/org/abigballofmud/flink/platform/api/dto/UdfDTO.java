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
public class UdfDTO implements Serializable {

    private static final long serialVersionUID = 854464206375410197L;

    public static final String FIELD_UDF_ID = "udf_id";

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
    @NotBlank
    private String content;
    @NotBlank
    private String clusterCode;
    private String udfStatus;

    private Long tenantId;
    private Long objectVersionNumber;
    private LocalDateTime creationDate;
    private Long createdBy;
    private LocalDateTime lastUpdateDate;
    private Long lastUpdatedBy;

}
