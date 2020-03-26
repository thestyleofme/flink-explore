package org.abigballofmud.flink.api.request;

import org.springframework.util.Assert;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 10:30
 * @since 1.0
 */
public class JarSubmitFlinkRequest extends AbstractSubmitRequest {

    /**
     *  是否需要cache 下载好的jar包
     */
    private boolean cache;

    private Integer parallelism;

    private String programArgs;

    private String entryClass;

    private String jarPath;

    private String savepointPath;

    private Boolean allowNonRestoredState;

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public String getProgramArgs() {
        return programArgs;
    }

    public void setProgramArgs(String programArgs) {
        this.programArgs = programArgs;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public void setEntryClass(String entryClass) {
        this.entryClass = entryClass;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getSavepointPath() {
        return savepointPath;
    }

    public void setSavepointPath(String savepointPath) {
        this.savepointPath = savepointPath;
    }

    public Boolean getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public void setAllowNonRestoredState(Boolean allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
    }

    @Override
    public void validate() {
        Assert.notNull(parallelism, "并发数不能为空");
        Assert.notNull(entryClass, "main函数不能为空");
    }
}
