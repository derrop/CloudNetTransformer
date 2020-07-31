package com.github.derrop.cloudnettransformer.cloudnet2.files.server;

import com.github.derrop.cloudnettransformer.Constants;
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
public class CloudNet2StaticServices implements CloudReaderWriter {

    private Path serversDirectory(Path directory) {
        return directory.resolve(Constants.WRAPPER_DIRECTORY).resolve("local").resolve("servers");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (cloudSystem.getStaticServices().isEmpty()) {
            return true;
        }

        Path baseDirectory = this.serversDirectory(directory);

        for (StaticServiceDirectory staticService : cloudSystem.getStaticServices()) {
            Path serverDirectory = baseDirectory.resolve(staticService.getTask()).resolve(staticService.getTask() + "-" + staticService.getId());

            staticService.copyTo(serverDirectory);
        }

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        Path baseDirectory = this.serversDirectory(directory);

        if (Files.notExists(baseDirectory)) {
            return true;
        }

        try (DirectoryStream<Path> groupStream = Files.newDirectoryStream(baseDirectory)) {
            for (Path groupDirectory : groupStream) {
                try (DirectoryStream<Path> serverStream = Files.newDirectoryStream(groupDirectory)) {
                    for (Path serverDirectory : serverStream) {
                        if (!Files.isDirectory(groupDirectory) || !Files.isDirectory(serverDirectory)) {
                            continue;
                        }

                        String group = groupDirectory.getFileName().toString();
                        String server = serverDirectory.getFileName().toString();

                        if (server.length() <= group.length() + 1) {
                            continue;
                        }
                        String idString = server.substring(group.length() + 1);

                        int id;
                        try {
                            id = Integer.parseInt(idString);
                        } catch (NumberFormatException exception) {
                            continue;
                        }

                        cloudSystem.getStaticServices().add(new StaticServiceDirectory(group, id, serverDirectory));
                    }
                }
            }
        }

        return true;
    }
}
