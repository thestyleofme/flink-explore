package org.abigballofmud.flink.api.domain.jars;

import java.util.List;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/27 14:05
 * @since 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class JarRunRequest {

    /**
     * entryClass : org.apache.flink.streaming.examples.wordcount.WordCount
     * parallelism : 1
     * programArg :
     * savepointPath :
     * allowNonRestoredState : true
     */
    @NotBlank
    private String entryClass;
    private Integer parallelism;
    private List<String> programArgsList;
    private String savepointPath;
    private Boolean allowNonRestoredState;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String jarId;
}
