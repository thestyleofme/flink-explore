package org.abigballofmud.flink.sqlsubmit.cli;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/10 16:51
 * @since 1.0
 */
public class CliOptions {

    private final String sqlFilePath;
    private final String workingSpace;

    public CliOptions(String sqlFilePath, String workingSpace) {
        this.sqlFilePath = sqlFilePath;
        this.workingSpace = workingSpace;
    }

    public String getSqlFilePath() {
        return sqlFilePath;
    }

    public String getWorkingSpace() {
        return workingSpace;
    }
}
