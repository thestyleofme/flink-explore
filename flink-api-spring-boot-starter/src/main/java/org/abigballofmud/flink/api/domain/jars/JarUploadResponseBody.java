package org.abigballofmud.flink.api.domain.jars;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 22:47
 * @since 1.0
 */
@NoArgsConstructor
@Data
public class JarUploadResponseBody {


    /**
     * filename : /data/flink/upload_jars/flink-web-upload/184ffd19-0280-4aa8-8a64-fd5bc91a36e0_WordCount.jar
     * status : success
     */
    private String filename;
    private String status;
}
