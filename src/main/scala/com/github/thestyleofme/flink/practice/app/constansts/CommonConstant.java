package com.github.thestyleofme.flink.practice.app.constansts;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/27 12:35
 * @since 1.0
 */
public class CommonConstant {

    private CommonConstant() {
    }

    public static final String INSERT = "INSERT";
   public static final String UPDATE = "UPDATE";
   public static final String UPSERT  = "UPSERT";
   public static final String DELETE = "DELETE";

   public static final String KAFKA_INIT_OFFSET_LATEST = "latest";
   public static final String KAFKA_INIT_OFFSET_EARLIEST = "earliest";
}
