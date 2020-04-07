package org.abigballofmud.flink.platform.infra.constants;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/03 18:16
 * @since 1.0
 */
public class CommonConstant {

    private CommonConstant() {
        throw new IllegalStateException("constant class");
    }

    public static class Status {
        private Status() {
            throw new IllegalStateException("constant class");
        }

        public static final String UPLOADING = "UPLOADING";
        public static final String UPLOADED = "UPLOADED";
    }

    /**
     * 这里存储内置的flink jar
     * 内置的jarCode都是下划线开头的
     */
    public static class JarCode {
        private JarCode() {
            throw new IllegalStateException("constant class");
        }

        public static final String FLINK_SQL_PLATFORM = "_FLINK_SQL_PLATFORM";
    }
}
