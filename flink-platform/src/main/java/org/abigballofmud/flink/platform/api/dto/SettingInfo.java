package org.abigballofmud.flink.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/07 22:39
 * @since 1.0
 */
public class SettingInfo {

    private SettingInfo() {
    }

    @Builder
    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SqlJobSettingInfo {
        /**
         * sql任务的一些额外配置，如：
         * parallelism : 1
         * allowNonRestoredState : true
         */
        @Builder.Default
        private Integer parallelism = 1;
        @Builder.Default
        private Boolean allowNonRestoredState = true;
        /**
         * 可覆盖upload jar中以前设置的entryClass
         */
        private String entryClass;
    }
}
