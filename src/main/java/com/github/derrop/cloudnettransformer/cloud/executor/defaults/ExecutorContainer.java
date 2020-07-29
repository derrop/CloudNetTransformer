package com.github.derrop.cloudnettransformer.cloud.executor.defaults;

import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;

import java.util.Arrays;

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

    public boolean hasType(ExecutorType type) {
        return Arrays.stream(this.description.types()).anyMatch(allowedType -> allowedType == type);
    }

    public String getName() {
        return this.executor.getOverriddenName() == null ? this.description.name() : this.executor.getOverriddenName();
    }

}
