package com.github.derrop.cloudnettransformer.cloudnet2.groups;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.*;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloudnet2.CloudNet2Utils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "Groups")
public class CloudNet2Groups implements CloudReaderWriter {

    private Path groupsDirectory(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("groups");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {

        Path groupsDirectory = this.groupsDirectory(directory);
        Files.createDirectories(groupsDirectory);

        for (ServiceTask task : cloudSystem.getTasks()) {
            if (task.getEnvironment() != ServiceEnvironment.MINECRAFT_SERVER) {
                continue;
            }

            Document group = Documents.newDocument();

            boolean signs = cloudSystem.getSignConfiguration().getConfigurations().stream().anyMatch(signConfig -> signConfig.getTargetGroup().equals(task.getName()));

            String groupMode = task.isStaticServices() ? "STATIC" : "DYNAMIC";
            if (signs) {
                groupMode = task.isStaticServices() ? "STATIC_LOBBY" : "LOBBY";
            }

            group
                    .append("name", task.getName())
                    .append("wrapper", task.getNodes())
                    .append("kickedForceFallback", true)
                    .append("serverType", "BUKKIT")
                    .append("groupMode", groupMode)
                    .append("globalTemplate", CloudNet2Utils.templateToJson(cloudSystem, task, new ServiceTemplate("local", task.getName(), "globaltemplate"), true))
                    .append("templates", task.getTemplates().stream().map(template -> CloudNet2Utils.templateToJson(cloudSystem, task, template, false)).collect(Collectors.toList()))
                    .append("memory", task.getMaxMemory())
                    .append("dynamicMemory", -1)
                    .append("maintenance", task.isMaintenance())
                    .append("minOnlineServers", task.getMinServices())
                    .append("maxOnlineServers", task.getMaxServices())
                    .append("advancedServerConfig", Documents.newDocument()
                            .append("notifyPlayerUpdatesFromNoCurrentPlayer", true)
                            .append("notifyProxyUpdates", true)
                            .append("notifyServerUpdates", true)
                            .append("disableAutoSavingForWorlds", !task.isStaticServices())
                    )
                    .append("percentForNewServerAutomatically", -1)
                    .append("priorityService", Documents.newDocument()
                            .append("stopTimeInSeconds", -1)
                            .append("global", Documents.newDocument().append("onlineServers", 0).append("onlineCount", 100))
                            .append("group", Documents.newDocument().append("onlineServers", 0).append("onlineCount", 100))
                    )
                    .append("settings", Documents.newDocument());

            // TODO implement priorityService (or smartConfig in CloudNet 3)?

            Documents.newDocument("group", group).json().write(groupsDirectory.resolve(task.getName() + ".json"));;
        }

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {

        Path groupsDirectory = this.groupsDirectory(directory);
        if (Files.notExists(groupsDirectory)) {
            return true;
        }


        try (DirectoryStream<Path> stream = Files.newDirectoryStream(groupsDirectory)) {
            for (Path path : stream) {
                Document group = Documents.jsonStorage().read(path).getDocument("group");
                if (group == null) {
                    continue;
                }

                String name = group.getString("name");
                String groupMode = group.getString("groupMode");

                Collection<Document> templates = group.getDocuments("templates");
                Document globalTemplate = group.getDocument("globalTemplate");

                cloudSystem.getTasks().add(new ServiceTask(
                        name,
                        templates.stream()
                                .map(document -> new ServiceTemplate("local", name, document.getString("name")))
                                .collect(Collectors.toList()),
                        templates.stream().flatMap(template -> CloudNet2Utils.asInclusions(template).stream()).collect(Collectors.toList()),
                        Collections.emptyList(),
                        templates.stream()
                                .map(document -> document.get("processPreParameters", String[].class))
                                .flatMap(Arrays::stream)
                                .collect(Collectors.toList()),
                        group.getBoolean("maintenance"),
                        groupMode.equals("STATIC_LOBBY") || groupMode.equals("STATIC"),
                        group.get("wrapper", TypeToken.getParameterized(Collection.class, String.class).getType()),
                        Arrays.asList(name, Constants.GLOBAL_SERVER_GROUP),
                        group.getInt("memory"),
                        44955,
                        group.getInt("minOnlineServers"),
                        group.getInt("maxOnlineServers"),
                        ServiceEnvironment.MINECRAFT_SERVER,
                        TemplateInstallerType.BALANCED,
                        Documents.newDocument()
                ));


                ServiceGroup serviceGroup = new ServiceGroup(
                        name,
                        Collections.singletonList(new ServiceTemplate("local", name, "globaltemplate")),
                        CloudNet2Utils.asInclusions(globalTemplate),
                        Collections.emptyList(),
                        globalTemplate.get("processPreParameters", TypeToken.getParameterized(Collection.class, String.class).getType()),
                        Collections.emptyList()
                );
                serviceGroup.setSupportsSigns(groupMode.equals("LOBBY") || groupMode.equals("STATIC_LOBBY"));

                cloudSystem.getGroups().add(serviceGroup);

            }
        }


        return true;
    }
}
