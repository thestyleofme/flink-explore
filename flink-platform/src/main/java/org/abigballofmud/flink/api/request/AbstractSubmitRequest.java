package org.abigballofmud.flink.api.request;

import org.abigballofmud.flink.common.Resource;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 10:27
 * @since 1.0
 */
public abstract class AbstractSubmitRequest implements FlinkRequest, SubmitRequest {

    private String jobName;

    private Resource resource;

    private boolean yarn;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setYarn(boolean yarn) {
        this.yarn = yarn;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public boolean isYarn() {
        return yarn;
    }

    /**
     * 校验
     */
    public abstract void validate();
}
