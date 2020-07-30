package com.github.derrop.cloudnettransformer.cloud.cloudnet3.service;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory.StaticServiceDirectory;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "StaticServices", priority = ExecutorPriority.FIRST)
public class CloudNet3StaticServices implements CloudReaderWriter {

    private Path servicesDirectory(Path directory) {
        return directory.resolve("local").resolve("services");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (cloudSystem.getStaticServices().isEmpty()) {
            return true;
        }

        Path baseDirectory = this.servicesDirectory(directory);

        for (StaticServiceDirectory staticService : cloudSystem.getStaticServices()) {
            Path serviceDirectory = baseDirectory.resolve(staticService.getTask() + "-" + staticService.getId());

            staticService.copyTo(serviceDirectory);
        }

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        Path baseDirectory = this.servicesDirectory(directory);

        if (Files.notExists(baseDirectory)) {
            return true;
        }

        try (DirectoryStream<Path> groupStream = Files.newDirectoryStream(baseDirectory)) {
            for (Path serverDirectory : groupStream) {
                if (!Files.isDirectory(serverDirectory)) {
                    continue;
                }

                String service = serverDirectory.getFileName().toString();

                int index = service.lastIndexOf('-');
                if (index == -1) {
                    continue;
                }
                String task = service.substring(0, index);
                String idString = service.substring(index + 1);
                if (task.isEmpty() || idString.isEmpty()) {
                    continue;
                }

                int id;
                try {
                    id = Integer.parseInt(idString);
                } catch (NumberFormatException exception) {
                    continue;
                }

                cloudSystem.getStaticServices().add(new StaticServiceDirectory(task, id, serverDirectory));
            }
        }

        return true;
    }
}
