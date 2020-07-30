package com.github.derrop.cloudnettransformer.cloud.cloudnet2.signs;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageCategory;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceGroup;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.*;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.documents.DefaultDocument;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.JsonElement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@DescribedCloudExecutor(name = "SignLayout")
public class CloudNet2SignLayout implements CloudReaderWriter {

    private Path config(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("local").resolve("signLayout.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) {

        SignConfiguration signConfiguration = cloudSystem.getSignConfiguration();
        if (signConfiguration == null || signConfiguration.getConfigurations().isEmpty()) {
            return false;
        }

        Document config = Documents.newDocument();

        GroupSignConfiguration mainSignConfiguration = signConfiguration.getConfigurations().iterator().next();
        config
                .append("fullServerHide", mainSignConfiguration.isHideFullServers())
                .append("knockbackOnSmallDistance", mainSignConfiguration.getKnockbackDistance() != 0 && mainSignConfiguration.getKnockbackStrength() != 0)
                .append("distance", mainSignConfiguration.getKnockbackDistance())
                .append("strength", mainSignConfiguration.getKnockbackStrength());

        Collection<Document> groupLayouts = new ArrayList<>();
        for (GroupSignConfiguration configuration : signConfiguration.getConfigurations()) {
            groupLayouts.add(this.asJson("default", configuration.getGlobalLayout()));
            for (TaskSignLayout taskLayout : configuration.getTaskLayouts()) {
                groupLayouts.add(this.asJson(taskLayout.getTask(), taskLayout));
            }
        }
        config.append("groupLayouts", groupLayouts);

        SignLayout[] searchLayouts = mainSignConfiguration.getSearchLayout().getSignLayouts().toArray(new SignLayout[0]);
        Document[] searchLayoutJson = new DefaultDocument[searchLayouts.length];
        for (int i = 0; i < searchLayouts.length; i++) {
            searchLayoutJson[i] = this.asJson("loading" + (i + 1), searchLayouts[i]);
        }
        config.append("searchingAnimation",
                Documents.newDocument()
                        .append("animations", mainSignConfiguration.getSearchLayout().getSignLayouts().size())
                        .append("animationsPerSecond", mainSignConfiguration.getSearchLayout().getAnimationsPerSecond())
                        .append("searchingLayouts", searchLayoutJson)
        );

        Documents.jsonStorage().write(Documents.newDocument("layout_config", config), this.config(directory));

        return true;
    }

    private Document asJson(String name, TaskSignLayout layout) {
        return Documents.newDocument()
                .append("name", name)
                .append("layouts", Arrays.asList(
                        this.asJson("empty", layout.getEmptyLayout()),
                        this.asJson("online", layout.getOnlineLayout()),
                        this.asJson("full", layout.getFullLayout()),
                        this.asJson("maintenance", layout.getMaintenanceLayout())
                ));
    }

    private Document asJson(String type, SignLayout layout) {
        return Documents.newDocument()
                .append("name", type)
                .append("signLayout", layout == null ? new String[]{"", "", "", ""} : layout.getLines())
                .append("blockId", 0)
                .append("blockName", layout == null ? "STONE" : layout.getBlockType())
                .append("subId", layout == null ? 0 : layout.getSubId());
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {

        Path configPath = this.config(directory);
        if (Files.notExists(configPath)) {
            return false;
        }

        Document config = Documents.jsonStorage().read(configPath).getDocument("layout_config");
        if (config == null) {
            return false;
        }

        boolean switchToSearchingWhenFull = config.getBoolean("fullServerHide");
        boolean knockbackEnabled = config.getBoolean("knockbackOnSmallDistance");
        double knockbackDistance = knockbackEnabled ? config.getDouble("distance") : 0;
        double knockbackStrength = knockbackEnabled ? config.getDouble("strength") : 0;

        TaskSignLayout globalLayout = null;
        Collection<TaskSignLayout> layouts = new ArrayList<>();

        for (JsonElement element : config.getJsonArray("groupLayouts")) {
            DefaultDocument groupLayout = new DefaultDocument(element);

            SignLayout empty = null;
            SignLayout online = null;
            SignLayout full = null;
            SignLayout maintenance = null;
            for (JsonElement layout : groupLayout.getJsonArray("layouts")) {
                DefaultDocument layoutDoc = new DefaultDocument(layout);
                switch (layoutDoc.getString("name")) {
                    case "empty":
                        empty = this.asSignLayout(layoutDoc);
                        break;

                    case "online":
                        online = this.asSignLayout(layoutDoc);
                        break;

                    case "full":
                        full = this.asSignLayout(layoutDoc);
                        break;

                    case "maintenance":
                        maintenance = this.asSignLayout(layoutDoc);
                        break;
                }
            }

            TaskSignLayout layout = new TaskSignLayout(groupLayout.getString("name"), online, empty, maintenance, full);

            if (layout.getTask().equalsIgnoreCase("default")) {
                globalLayout = layout;
            } else {
                layouts.add(layout);
            }
        }

        AnimatedSignLayout searchLayout = new AnimatedSignLayout(
                StreamSupport.stream(config.getDocument("searchingAnimation").getJsonArray("searchingLayouts").spliterator(), false)
                        .map(DefaultDocument::new)
                        .map(this::asSignLayout)
                        .collect(Collectors.toList()),
                config.getDocument("searchingAnimation").getInt("animationsPerSecond")
        );

        cloudSystem.setMessage(MessageType.SIGN_SERVER_CONNECTING, ""); // CloudNet 2 doesn't have the connecting message

        Collection<GroupSignConfiguration> configurations = new ArrayList<>();
        for (ServiceGroup group : cloudSystem.getGroups()) {
            if (group.isSupportingSigns()) {
                configurations.add(new GroupSignConfiguration(
                        group.getName(),
                        switchToSearchingWhenFull, knockbackDistance, knockbackStrength,
                        layouts, globalLayout,
                        searchLayout, searchLayout
                ));
            }
        }
        cloudSystem.setSignConfiguration(new SignConfiguration(configurations, cloudSystem.getMessages(MessageCategory.SIGNS)));

        return true;
    }

    private SignLayout asSignLayout(DefaultDocument document) {
        return new SignLayout(
                document.get("signLayout", String[].class),
                document.getString("blockType") != null ? document.getString("blockType") : String.valueOf(document.getInt("blockId")),
                document.getShort("subId")
        );
    }

}
