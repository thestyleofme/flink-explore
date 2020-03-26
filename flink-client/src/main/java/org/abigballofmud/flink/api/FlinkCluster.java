package org.abigballofmud.flink.api;

import java.util.Set;

import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 22:17
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FlinkCluster {

    private Long clusterId;

    /**
     * jobManager地址
     */
    private String jobManagerUrl;
    /**
     * 若配置了Ha，这里是备用的jm
     */
    private Set<String> jobManagerStandbyUrlSet;
    /**
     * flink web上传jar的path，可设置，web.upload.dir: /data/flink/upload_jars
     */
    private String uploadJarPath;

}
