package org.abigballofmud.flink.api.constants;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/27 1:11
 * @since 1.0
 */
public final class FlinkApiConstant {

    private FlinkApiConstant() {
    }

    public static final class Jars {
        private Jars() {
        }

        /**
         * flink jar upload url
         */
        public static final String UPLOAD_JAR = "/v1/jars/upload";
        /**
         * running a jar previously uploaded via '/jars/upload'
         */
        public static final String RUN_JAR = "/v1/jars/%s/run";
    }

    public static final class Jobs {
        private Jobs() {
        }
        /**
         * flink job url
         */
        public static final String JOB = "/v1/jobs";
    }
}
