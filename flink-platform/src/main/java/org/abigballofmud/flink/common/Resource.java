package org.abigballofmud.flink.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:04
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resource implements Serializable {

    private static final long serialVersionUID = 354330068317561841L;

    private String queue;

    private Long memory;

    private Long vCores;

}
