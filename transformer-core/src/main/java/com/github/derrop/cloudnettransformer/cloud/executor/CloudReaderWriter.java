package com.github.derrop.cloudnettransformer.cloud.executor;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;

import java.io.IOException;
import java.nio.file.Path;

public interface CloudReaderWriter extends CloudExecutor {

    ExecuteResult write(CloudSystem cloudSystem, Path directory) throws IOException;

    ExecuteResult read(CloudSystem cloudSystem, Path directory) throws IOException;

    @Override
    default ExecuteResult execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {
        switch (type) {
            case READ:
                return this.read(cloudSystem, directory);
            case WRITE:
                return this.write(cloudSystem, directory);
            default:
                return ExecuteResult.failed("Unknown ExecutorType: " + type);
        }
    }
}
