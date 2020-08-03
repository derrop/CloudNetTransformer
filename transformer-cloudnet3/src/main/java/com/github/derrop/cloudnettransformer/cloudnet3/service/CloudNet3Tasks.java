package com.github.derrop.cloudnettransformer.cloudnet3.service;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.UserNote;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.*;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.cloudnettransformer.cloudnet3.CloudNet3Utils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "Tasks", priority = ExecutorPriority.FIRST)
public class CloudNet3Tasks implements CloudReaderWriter {

    private Path tasksDirectory(Path directory) {
        return directory.resolve("local").resolve("tasks");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {

        Path tasksDirectory = this.tasksDirectory(directory);
        Files.createDirectories(tasksDirectory);

        for (ServiceTask task : cloudSystem.getTasks()) {

            Document document = Documents.newDocument();

            String templateInstaller = "INSTALL_ALL";
            switch (task.getTemplateInstallerType()) {
                case BALANCED:
                    templateInstaller = "INSTALL_BALANCED";
                    break;
                case ONE_RANDOM:
                    templateInstaller = "INSTALL_RANDOM_ONCE";
                    break;
                case MANY_RANDOM:
                    templateInstaller = "INSTALL_RANDOM";
                    break;
            }

            document
                    .append("name", task.getName())
                    .append("runtime", "jvm")
                    .append("maintenance", task.isMaintenance())
                    .append("autoDeleteOnStop", true)
                    .append("staticServices", task.isStaticServices())
                    .append("associatedNodes", task.getNodes())
                    .append("groups", task.getGroups())
                    .append("deletedFilesAfterStop", Collections.emptyList())
                    .append("processConfiguration", Documents.newDocument()
                            .append("environment", task.getEnvironment())
                            .append("maxHeapMemorySize", task.getMaxMemory())
                            .append("jvmOptions", task.getJvmOptions())
                    )
                    .append("startPort", task.getStartPort())
                    .append("minServiceCount", task.getMinServices())
                    .append("includes", task.getInclusions().stream().map(CloudNet3Utils::inclusionToDocument).collect(Collectors.toList()))
                    .append("templates", task.getTemplates())
                    .append("deployments", task.getDeployments())
                    .append("properties", Documents.newDocument().append("smartConfig", Documents.newDocument()
                            .append("enabled", task.getTemplateInstallerType() != TemplateInstallerType.ALL || task.getMaxServices() != -1)
                            .append("templateInstaller", templateInstaller)
                            .append("percentOfPlayersForANewServiceByInstance", task.getPlayersPercentForNewServer())
                            .append("forAnewInstanceDelayTimeInSeconds", -1)
                            .append("minNonFullServices", task.getPlayersPercentForNewServer() < 0 ? 0 : 1)
                            .append("maxServiceCount", task.getMaxServices())
                    ));

            document.json().write(tasksDirectory.resolve(task.getName() + ".json"));;

        }

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {

        Path tasksDirectory = this.tasksDirectory(directory);
        if (Files.notExists(tasksDirectory)) {
            return true;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tasksDirectory)) {
            for (Path path : stream) {
                Document task = Documents.jsonStorage().read(path);
                if (!task.getString("runtime").equals("jvm")) {
                    cloudSystem.addNote(UserNote.normal("Runtime '" + task.getString("runtime") + "' of the task '" + task.getString("name") + "' is not supported, the task will not be transformed"));
                    continue;
                }

                Document processConfig = task.getDocument("processConfiguration");
                if (processConfig == null) {
                    continue;
                }
                ServiceEnvironment environment = processConfig.get("environment", ServiceEnvironment.class);
                if (environment == null) {
                    cloudSystem.addNote(UserNote.normal("Environment '" + processConfig.getString("environment") + "' of the task '" + task.getString("name") + "' is not supported, the task will not be transformed"));
                    continue;
                }

                Document properties = task.getDocument("properties");
                Document smartConfig = properties != null ? properties.getDocument("smartConfig") : null;
                boolean smartConfigEnabled = smartConfig != null && smartConfig.getBoolean("enabled");

                TemplateInstallerType templateInstaller = smartConfigEnabled ? smartConfig.get("templateInstaller", TemplateInstallerType.class) : null;
                if (templateInstaller == null) {
                    templateInstaller = TemplateInstallerType.ALL;
                }

                cloudSystem.getTasks().add(new ServiceTask(
                        task.getString("name"),
                        task.get("templates", TypeToken.getParameterized(Collection.class, ServiceTemplate.class).getType()),
                        task.getDocuments("includes").stream().map(CloudNet3Utils::documentToInclusion).collect(Collectors.toList()),
                        task.get("deployments", TypeToken.getParameterized(Collection.class, ServiceDeployment.class).getType()),
                        processConfig.get("jvmOptions", TypeToken.getParameterized(Collection.class, String.class).getType()),
                        task.getBoolean("maintenance"),
                        task.getBoolean("staticServices"),
                        task.get("associatedNodes", TypeToken.getParameterized(Collection.class, String.class).getType()),
                        task.get("groups", TypeToken.getParameterized(Collection.class, String.class).getType()),
                        processConfig.getInt("maxHeapMemorySize"),
                        task.getInt("startPort"),
                        task.getInt("minServiceCount"),
                        smartConfigEnabled ? smartConfig.getInt("maxServiceCount") : -1,
                        smartConfigEnabled ? smartConfig.getInt("percentOfPlayersForANewServiceByInstance") : -1,
                        environment,
                        templateInstaller,
                        Documents.newDocument()
                ));

            }
        }

        return true;
    }
}
