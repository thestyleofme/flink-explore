package com.github.thestyleofme.flink.practice.sqlsubmit.udf.scalarfunc;

import org.apache.flink.table.functions.ScalarFunction;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 17:46
 * @since 1.0
 */
public class HashCode extends ScalarFunction {

    private static final long serialVersionUID = -2393568031522542438L;
    private int factor = 12;

    public HashCode() {
    }

    public HashCode(int factor) {
        this.factor = factor;
    }

    public int eval(String s) {
        return s.hashCode() * factor;
    }
}
