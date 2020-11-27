package com.github.derrop.cloudnettransformer.cloudnet2.signs;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.PlaceholderType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceGroup;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.AnimatedSignLayout;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.GroupSignConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.SignConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.SignLayout;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.TaskSignLayout;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@DescribedCloudExecutor(name = "SignLayout")
public class CloudNet2SignLayout implements CloudReaderWriter {

    private Path config(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("local").resolve("signLayout.json");
    }

    @Override
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) {
        SignConfiguration signConfiguration = cloudSystem.getSignConfiguration();
        if (signConfiguration == null || signConfiguration.getConfigurations().isEmpty()) {
            return ExecuteResult.failed("No SignConfiguration (or one without any Entries) set in the CloudSystem");
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
            groupLayouts.add(this.asJson(cloudSystem, "default", configuration.getGlobalLayout()));
            for (TaskSignLayout taskLayout : configuration.getTaskLayouts()) {
                groupLayouts.add(this.asJson(cloudSystem, taskLayout.getTask(), taskLayout));
            }
        }
        config.append("groupLayouts", groupLayouts);

        SignLayout[] searchLayouts = mainSignConfiguration.getSearchLayout().getSignLayouts().toArray(new SignLayout[0]);
        Document[] searchLayoutJson = new DefaultDocument[searchLayouts.length];
        for (int i = 0; i < searchLayouts.length; i++) {
            searchLayoutJson[i] = this.asJson(cloudSystem, "loading" + (i + 1), searchLayouts[i]);
        }
        config.append("searchingAnimation",
                Documents.newDocument()
                        .append("animations", mainSignConfiguration.getSearchLayout().getSignLayouts().size())
                        .append("animationsPerSecond", mainSignConfiguration.getSearchLayout().getAnimationsPerSecond())
                        .append("searchingLayouts", searchLayoutJson)
        );

        Documents.newDocument("layout_config", config).json().write(this.config(directory));
        ;

        return ExecuteResult.success();
    }

    private Document asJson(CloudSystem cloudSystem, String name, TaskSignLayout layout) {
        return Documents.newDocument()
                .append("name", name)
                .append("layouts", Arrays.asList(
                        this.asJson(cloudSystem, "empty", layout.getEmptyLayout()),
                        this.asJson(cloudSystem, "online", layout.getOnlineLayout()),
                        this.asJson(cloudSystem, "full", layout.getFullLayout()),
                        this.asJson(cloudSystem, "maintenance", layout.getMaintenanceLayout())
                ));
    }

    private Document asJson(CloudSystem cloudSystem, String type, SignLayout layout) {
        Map<PlaceholderType, String> placeholders = new HashMap<>();
        this.fillPlaceholders(placeholders);

        String[] lines = layout == null ? new String[]{"", "", "", ""} : layout.getLines();
        for (int i = 0; i < lines.length; i++) {
            lines[i] = cloudSystem.updatePlaceholders(lines[i], placeholders);
        }
        return Documents.newDocument()
                .append("name", type)
                .append("signLayout", lines)
                .append("blockId", 0)
                .append("blockName", layout == null ? "STONE" : layout.getBlockType())
                .append("subId", layout == null ? 0 : layout.getSubId());
    }

    private void fillPlaceholders(Map<PlaceholderType, String> map) {
        // online/empty/full layout
        map.put(PlaceholderType.SIGNS_NAME, "%server%");
        map.put(PlaceholderType.SIGNS_TASK_ID, "%id%");
        map.put(PlaceholderType.SIGNS_HOST, "%host%");
        map.put(PlaceholderType.SIGNS_PORT, "%port%");
        map.put(PlaceholderType.SIGNS_MEMORY, "%memory%");
        map.put(PlaceholderType.SIGNS_ONLINE_PLAYERS, "%online_players%");
        map.put(PlaceholderType.SIGNS_MAX_PLAYERS, "%max_players%");
        map.put(PlaceholderType.SIGNS_MOTD, "%motd%");
        map.put(PlaceholderType.SIGNS_STATE, "%state%");
        map.put(PlaceholderType.SIGNS_NODE, "%wrapper%");
        map.put(PlaceholderType.SIGNS_EXTRA, "%extra%");
        map.put(PlaceholderType.SIGNS_TEMPLATE, "%template%");
        map.put(PlaceholderType.SIGNS_TASK, "%group%");

        // loading/maintenance layout
        map.put(PlaceholderType.SIGNS_TARGET_GROUP, "%group%");
        map.put(PlaceholderType.SIGNS_PLACED_GROUP, "%from%");
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) {

        Path configPath = this.config(directory);
        if (Files.notExists(configPath)) {
            return ExecuteResult.failed("Config '" + configPath + "' not found");
        }

        Document config = Documents.jsonStorage().read(configPath).getDocument("layout_config");
        if (config == null) {
            return ExecuteResult.failed("No layout_config entry found in the SignLayout in " + configPath);
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
        cloudSystem.setSignConfiguration(new SignConfiguration(configurations));

        this.fillPlaceholders(cloudSystem.getPlaceholders());

        return ExecuteResult.success();
    }

    private SignLayout asSignLayout(DefaultDocument document) {
        return new SignLayout(
                document.get("signLayout", String[].class),
                document.getString("blockType") != null ? document.getString("blockType") : String.valueOf(document.getInt("blockId")),
                document.getShort("subId")
        );
    }

}
