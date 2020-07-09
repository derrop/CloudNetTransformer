package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.*;
import com.github.derrop.cloudnettransformer.cloud.reader.CloudReader;
import com.github.derrop.cloudnettransformer.cloud.writer.CloudWriter;
import com.github.derrop.cloudnettransformer.document.Document;
import com.github.derrop.cloudnettransformer.document.Documents;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CloudNet3Signs implements CloudReader, CloudWriter {
    @Override
    public String getName() {
        return "Signs";
    }

    private Path config(Path directory) {
        return directory.resolve("modules").resolve("CloudNet-Signs").resolve("config.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) {
        if (cloudSystem.getSignConfiguration() == null) {
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
        messages.put("server-connecting-message", signConfiguration.getMessages().get(SignMessage.SERVER_CONNECTING));
        messages.put("command-cloudsign-remove-success", signConfiguration.getMessages().get(SignMessage.SIGN_REMOVE_SUCCESS));
        messages.put("command-cloudsign-create-success", signConfiguration.getMessages().get(SignMessage.SIGN_CREATE_SUCCESS));
        messages.put("command-cloudsign-cleanup-success", signConfiguration.getMessages().get(SignMessage.SIGN_CLEANUP_SUCCESS));
        messages.put("command-cloudsign-sign-already-exist", signConfiguration.getMessages().get(SignMessage.SIGN_ALREADY_EXISTS));
        document.append("messages", messages);

        Documents.jsonStorage().write(document, this.config(directory));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {
        Path configPath = this.config(directory);
        if (!Files.exists(configPath)) {
            return false;
        }

        Document document = Documents.newJsonDocument(configPath);
        if (!document.contains("config")) {
            return false;
        }

        Document config = document.getDocument("config");
        if (!config.contains("configurations")) {
            return false;
        }

        Collection<GroupSignConfiguration> configurations = new ArrayList<>();

        for (JsonElement element : config.getJsonArray("configurations")) {
            Document configuration = Documents.newDocument(element.getAsJsonObject());

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
        Map<SignMessage, String> outMessages = new HashMap<>(messages.size());
        outMessages.put(SignMessage.SERVER_CONNECTING, messages.get("server-connecting-message"));
        outMessages.put(SignMessage.SIGN_REMOVE_SUCCESS, messages.get("command-cloudsign-remove-success"));
        outMessages.put(SignMessage.SIGN_CREATE_SUCCESS, messages.get("command-cloudsign-create-success"));
        outMessages.put(SignMessage.SIGN_CLEANUP_SUCCESS, messages.get("command-cloudsign-cleanup-success"));
        outMessages.put(SignMessage.SIGN_ALREADY_EXISTS, messages.get("command-cloudsign-sign-already-exist"));

        cloudSystem.setSignConfiguration(new SignConfiguration(configurations, outMessages));

        return true;
    }
}
