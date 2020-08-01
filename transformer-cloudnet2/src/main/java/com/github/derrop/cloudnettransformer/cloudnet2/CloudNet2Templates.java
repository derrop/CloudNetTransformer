package com.github.derrop.cloudnettransformer.cloudnet2;

import com.github.derrop.cloudnettransformer.cloud.CloudNetTemplates;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceEnvironment;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTask;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;

import java.io.IOException;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "Templates", priority = ExecutorPriority.LAST + 1)
public abstract class CloudNet2Templates extends CloudNetTemplates {

    @Override
    protected void resolvePrefixDirectory(CloudSystem cloudSystem, Path prefixDirectory) throws IOException {
        for (ServiceTask task : cloudSystem.getTasks()) {
            if (task.getEnvironment() != ServiceEnvironment.BUNGEECORD) {
                continue;
            }

            String group = prefixDirectory.getFileName().toString();
            if (task.getTemplates().stream().anyMatch(template -> template.getPrefix().equals(group))) {
                cloudSystem.getTemplates().add(super.createTemplateDirectory(group, "default", prefixDirectory));
                return;
            }
        }
        super.resolvePrefixDirectory(cloudSystem, prefixDirectory);
    }

}
