package com.github.derrop.cloudnettransformer.cloud.cloudnet2.files;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;

import java.io.IOException;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "File exclusion", types = ExecutorType.READ)
public class CloudNet2FileExclusion implements CloudExecutor {
    @Override
    public boolean execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {
        cloudSystem.addExcludedServiceFiles("CloudNetAPI.jar");
        return true;
    }
}
