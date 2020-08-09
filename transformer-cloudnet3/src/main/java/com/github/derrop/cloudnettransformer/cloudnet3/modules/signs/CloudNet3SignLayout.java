package com.github.derrop.cloudnettransformer.cloudnet3.modules.signs;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.PlaceholderCategory;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceGroup;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.*;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.FileDownloader;
import com.github.derrop.cloudnettransformer.cloudnet3.CloudNet3Utils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "SignLayout")
public class CloudNet3SignLayout extends FileDownloader implements CloudReaderWriter {

    public CloudNet3SignLayout() {
        super("https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet-v3/job/master/lastSuccessfulBuild/artifact/cloudnet-modules/cloudnet-signs/build/libs/cloudnet-signs.jar", "modules/cloudnet-signs.jar");
    }

    private Path config(Path directory) {
        return directory.resolve("modules").resolve("CloudNet-Signs").resolve("config.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (cloudSystem.getSignConfiguration() == null) {
            return false;
        }
        if (!super.downloadFile(directory)) {
            return false;
        }

        SignConfiguration signConfiguration = cloudSystem.getSignConfiguration();

        Document document = Documents.newDocument();

        Collection<Document> configurations = signConfiguration.getConfigurations().stream()
                .map(configuration -> Documents.newDocument()
                        .append("targetGroup", configuration.getTargetGroup())
                        .append("switchToSearchingWhenServiceIsFull", configuration.isHideFullServers())
                        .append("knockbackDistance", configuration.getKnockbackDistance())
                        .append("knockbackStrength", configuration.getKnockbackStrength())
                        .append("taskLayouts", configuration.getTaskLayouts())
                        .append("defaultOnlineLayout", configuration.getGlobalLayout().getOnlineLayout())
                        .append("defaultEmptyLayout", configuration.getGlobalLayout().getEmptyLayout())
                        .append("defaultFullLayout", configuration.getGlobalLayout().getFullLayout())
                        .append("startingLayouts", configuration.getStartingLayout())
                        .append("searchingLayouts", configuration.getSearchLayout())
                ).collect(Collectors.toList());

        document.append("configurations", configurations);

        Map<String, String> messages = new HashMap<>();
        messages.put("server-connecting-message", cloudSystem.getMessage(MessageType.SIGN_SERVER_CONNECTING));
        messages.put("command-cloudsign-remove-success", cloudSystem.getMessage(MessageType.SIGN_REMOVE_SUCCESS));
        messages.put("command-cloudsign-create-success", cloudSystem.getMessage(MessageType.SIGN_CREATE_SUCCESS));
        messages.put("command-cloudsign-cleanup-success", cloudSystem.getMessage(MessageType.SIGN_CLEANUP_SUCCESS));
        messages.put("command-cloudsign-sign-already-exist", cloudSystem.getMessage(MessageType.SIGN_ALREADY_EXISTS));
        document.append("messages", messages);

        document.json().write(this.config(directory));;

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {
        cloudSystem.addExcludedServiceFiles("cloudnet-signs.jar");

        Path configPath = this.config(directory);
        if (Files.notExists(configPath)) {
            return false;
        }

        Document config = Documents.jsonStorage().read(configPath).getDocument("config");
        if (!config.contains("configurations")) {
            return false;
        }

        Collection<GroupSignConfiguration> configurations = new ArrayList<>();

        for (Document configuration : config.getDocuments("configurations")) {
            TaskSignLayout globalLayout = new TaskSignLayout(
                    null,
                    configuration.get("defaultOnlineLayout", SignLayout.class),
                    configuration.get("defaultEmptyLayout", SignLayout.class),
                    null,
                    configuration.get("defaultFullLayout", SignLayout.class)
            );
            Collection<TaskSignLayout> taskLayouts = configuration.get("taskLayouts", TypeToken.getParameterized(Collection.class, TaskSignLayout.class).getType());

            AnimatedSignLayout startingLayout = configuration.get("startingLayouts", AnimatedSignLayout.class);
            AnimatedSignLayout searchLayout = configuration.get("searchLayouts", AnimatedSignLayout.class);

            configurations.add(new GroupSignConfiguration(
                    configuration.getString("targetGroup"),
                    configuration.getBoolean("switchToSearchingWhenServiceIsFull"),
                    configuration.getDouble("knockbackDistance"),
                    configuration.getDouble("knockbackStrength"),
                    taskLayouts,
                    globalLayout,
                    searchLayout,
                    startingLayout
            ));
        }

        Map<String, String> messages = config.get("messages", TypeToken.getParameterized(Map.class, String.class, String.class).getType());
        cloudSystem.setMessage(MessageType.SIGN_SERVER_CONNECTING, messages.get("server-connecting-message"));
        cloudSystem.setMessage(MessageType.SIGN_REMOVE_SUCCESS, messages.get("command-cloudsign-remove-success"));
        cloudSystem.setMessage(MessageType.SIGN_CREATE_SUCCESS, messages.get("command-cloudsign-create-success"));
        cloudSystem.setMessage(MessageType.SIGN_CLEANUP_SUCCESS, messages.get("command-cloudsign-cleanup-success"));
        cloudSystem.setMessage(MessageType.SIGN_ALREADY_EXISTS, messages.get("command-cloudsign-sign-already-exist"));

        cloudSystem.setSignConfiguration(new SignConfiguration(configurations));

        for (GroupSignConfiguration configuration : configurations) {
            for (ServiceGroup group : cloudSystem.getGroups()) {
                if (group.getName().equals(configuration.getTargetGroup())) {
                    group.setSupportsSigns(true);
                }
            }
        }

        CloudNet3Utils.fillServiceInfoPlaceholders(PlaceholderCategory.SIGNS, cloudSystem.getPlaceholders());

        return true;
    }
}
