package org.abigballofmud.flink.platform.infra.enums;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 9:39
 * @since 1.0
 */
public enum UdfTypeEnum {

    /**
     * 上传jar的形式注册flink udf
     */
    JAR,
    /**
     * 直接写udf代码的方式
     */
    CODE;
}
