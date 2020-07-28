package com.github.derrop.cloudnettransformer.cloud.executor.defaults;

import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;

public class ExecutorContainer {

    private final CloudExecutor executor;
    private final DescribedCloudExecutor description;

    public ExecutorContainer(CloudExecutor executor, DescribedCloudExecutor description) {
        this.executor = executor;
        this.description = description;
    }

    public CloudExecutor getExecutor() {
        return this.executor;
    }

    public DescribedCloudExecutor getDescription() {
        return this.description;
    }

    public String getName() {
        return this.executor.getOverriddenName() == null ? this.description.name() : this.executor.getOverriddenName();
    }

}
