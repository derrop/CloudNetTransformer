package com.github.derrop.cloudnettransformer.cloudnet2.files;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;

import java.nio.file.Path;

@DescribedCloudExecutor(name = "File exclusion", types = ExecutorType.READ)
public class CloudNet2FileExclusion implements CloudExecutor {
    @Override
    public ExecuteResult execute(ExecutorType type, CloudSystem cloudSystem, Path directory) {
        cloudSystem.addExcludedServiceFiles("CloudNetAPI.jar");
        return ExecuteResult.success();
    }
}
