package org.abigballofmud.flink.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:18
 * @since 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    protected boolean success;

    protected String message;

    public Response(boolean success) {
        this.success = success;
    }

    public Response(String message) {
        this.message = message;
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
