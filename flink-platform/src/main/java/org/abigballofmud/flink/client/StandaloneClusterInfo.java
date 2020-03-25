package org.abigballofmud.flink.client;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 18:05
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandaloneClusterInfo {

    /**
     * jobManager地址
     */
    private String address;

    /**
     * 端口
     */
    private Integer port;

    private Map<String, Object> properties;

    /**
     * jobManager的web url
     */
    private String webInterfaceUrl;

}
