package org.abigballofmud.flink.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:19
 * @since 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmitFlinkResponse extends Response {

    private String jobId;

    public SubmitFlinkResponse(String message) {
        super(message);
    }

    public SubmitFlinkResponse(boolean success, String jobId) {
        super(success);
        this.jobId = jobId;
    }

}