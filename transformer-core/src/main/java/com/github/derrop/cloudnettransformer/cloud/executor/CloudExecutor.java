package com.github.derrop.cloudnettransformer.cloud.executor;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;

import java.io.IOException;
import java.nio.file.Path;

public interface CloudExecutor {

    default String getOverriddenName() {
        return null;
    }

    ExecuteResult execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException;

}
