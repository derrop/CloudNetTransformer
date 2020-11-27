package com.github.derrop.cloudnettransformer.cloudnet3.modules;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.PlaceholderType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.login.LoginConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdLayout;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist.TabListConfiguration;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.FileDownloader;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "SyncProxy", priority = ExecutorPriority.LAST)
public class CloudNet3SyncProxy extends FileDownloader implements CloudReaderWriter {

    public CloudNet3SyncProxy() {
        super("https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet-v3/job/master/lastSuccessfulBuild/artifact/cloudnet-modules/cloudnet-syncproxy/build/libs/cloudnet-syncproxy.jar", "modules/cloudnet-syncproxy.jar");
    }

    private Path config(Path directory) {
        return directory.resolve("modules").resolve("CloudNet-SyncProxy").resolve("config.json");
    }

    @Override
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (!super.downloadFile(directory)) {
            return ExecuteResult.failed("Failed to download file to " + directory);
        }

        Document document = Documents.newDocument()
                .append("loginConfigurations",
                        cloudSystem.getLoginConfigurations().stream()
                                .map(loginConfiguration -> {
                                    MotdConfiguration motdConfiguration = cloudSystem.getMotdConfigurations().stream()
                                            .filter(config -> config.getTargetGroup().equals(loginConfiguration.getTargetGroup()))
                                            .findFirst()
                                            .orElse(null);

                                    Document loginDoc = Documents.newDocument()
                                            .append("targetGroup", loginConfiguration.getTargetGroup())
                                            .append("maintenance", loginConfiguration.isMaintenance())
                                            .append("maxPlayers", loginConfiguration.getMaxPlayers())
                                            .append("whitelist", loginConfiguration.getWhitelist());

                                    if (motdConfiguration != null) {
                                        loginDoc
                                                .append("motds", motdConfiguration.getMotdLayouts().stream().map(layout -> this.asJson(cloudSystem, layout)).collect(Collectors.toList()))
                                                .append("maintenanceMotds", motdConfiguration.getMaintenanceLayouts().stream().map(layout -> this.asJson(cloudSystem, layout)).collect(Collectors.toList()));
                                    } else {
                                        loginDoc.append("motds", Collections.emptyList()).append("maintenanceMotds", Collections.emptyList());
                                    }

                                    return loginDoc;
                                }).collect(Collectors.toList())
                )
                .append("tabListConfigurations", cloudSystem.getTabListConfigurations())
                .append("ingameServiceStartStopMessages", cloudSystem.getConfig().shouldNotifyServerUpdates());

        Collection<Document> tabListConfigurations = new ArrayList<>();
        for (Document tabListConfiguration : document.getDocuments("tabListConfigurations")) {
            Collection<Document> entries = new ArrayList<>();
            for (Document entry : tabListConfiguration.getDocuments("entries")) {
                entries.add(Documents.newDocument()
                        .append("header", this.updateTabPlaceholders(cloudSystem, entry.getString("header")))
                        .append("footer", this.updateTabPlaceholders(cloudSystem, entry.getString("footer")))
                );
            }
            tabListConfiguration.append("entries", entries);

            tabListConfigurations.add(tabListConfiguration);
        }
        document.append("tabListConfigurations", tabListConfigurations);

        Documents.newDocument().append("config", document).json().write(this.config(directory));;

        return ExecuteResult.success();
    }

    private String updateTabPlaceholders(CloudSystem cloudSystem, String input) {
        Map<PlaceholderType, String> map = new HashMap<>();
        this.fillTabPlaceholders(map);
        return cloudSystem.updatePlaceholders(input, map);
    }

    private String updateLoginPlaceholders(CloudSystem cloudSystem, String input) {
        Map<PlaceholderType, String> map = new HashMap<>();
        this.fillLoginPlaceholders(map);
        return cloudSystem.updatePlaceholders(input, map);
    }

    private void fillTabPlaceholders(Map<PlaceholderType, String> map) {
        map.put(PlaceholderType.TAB_PROXY, "%proxy%");
        map.put(PlaceholderType.TAB_PROXY_ID, "%proxy_uniqueId%");
        map.put(PlaceholderType.TAB_SERVER, "%server%");
        map.put(PlaceholderType.TAB_ONLINE_PLAYERS, "%online_players%");
        map.put(PlaceholderType.TAB_MAX_PLAYERS, "%max_players%");
        map.put(PlaceholderType.TAB_PROXY_TASK, "%proxy_task_name%");
        map.put(PlaceholderType.TAB_PLAYER_NAME, "%name%");
        map.put(PlaceholderType.TAB_PLAYER_PING, "%ping%");
        map.put(PlaceholderType.TAB_TIME, "%time%");
        map.put(PlaceholderType.TAB_PERMISSION_GROUP_NAME, "%group%");
        map.put(PlaceholderType.TAB_PERMISSION_GROUP_PREFIX, "%prefix%");
        map.put(PlaceholderType.TAB_PERMISSION_GROUP_SUFFIX, "%suffix%");
        map.put(PlaceholderType.TAB_PERMISSION_GROUP_DISPLAY, "%display%");
        map.put(PlaceholderType.TAB_PERMISSION_GROUP_COLOR, "%color%");
    }

    private void fillLoginPlaceholders(Map<PlaceholderType, String> map) {
        map.put(PlaceholderType.MOTD_PROXY, "%proxy%");
        map.put(PlaceholderType.MOTD_PROXY_ID, "%proxy_uniqueId%");
        map.put(PlaceholderType.MOTD_TASK, "%task%");
        map.put(PlaceholderType.MOTD_NODE, "%node%");
        map.put(PlaceholderType.MOTD_ONLINE_PLAYERS, "%online_players%");
        map.put(PlaceholderType.MOTD_MAX_PLAYERS, "%max_players%");
    }

    private Document asJson(CloudSystem cloudSystem, MotdLayout layout) {
        return Documents.newDocument()
                .append("firstLine", this.updateLoginPlaceholders(cloudSystem, layout.getFirstLine()))
                .append("secondLine", this.updateLoginPlaceholders(cloudSystem, layout.getSecondLine()))
                .append("autoSlot", layout.isAutoSlot())
                .append("autoSlotMaxPlayersDistance", layout.getAutoSlotDistance())
                .append("playerInfo", layout.getPlayerInfo())
                .append("protocolText", layout.getProtocolText());
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) {
        cloudSystem.addExcludedServiceFiles("cloudnet-syncproxy.jar");

        Path configPath = this.config(directory);
        if (Files.notExists(configPath)) {
            return ExecuteResult.failed("Config '" + configPath + "' not found");
        }
        Document document = Documents.jsonStorage().read(configPath).getDocument("config");
        if (document == null) {
            return ExecuteResult.failed("Failed to read json from " + configPath);
        }

        for (Document login : document.getDocuments("loginConfigurations")) {
            cloudSystem.getLoginConfigurations().add(new LoginConfiguration(
                    login.getString("targetGroup"),
                    login.getBoolean("maintenance"),
                    login.getInt("maxPlayers"),
                    login.get("whitelist", TypeToken.getParameterized(Collection.class, String.class).getType())
            ));
            cloudSystem.getMotdConfigurations().add(new MotdConfiguration(
                    login.getString("targetGroup"),
                    login.getDocuments("motds").stream().map(this::asMotdLayout).collect(Collectors.toList()),
                    login.getDocuments("maintenanceMotds").stream().map(this::asMotdLayout).collect(Collectors.toList())
            ));
        }

        cloudSystem.getTabListConfigurations().addAll(document.get("tabListConfigurations", TypeToken.getParameterized(Collection.class, TabListConfiguration.class).getType()));

        Document messages = document.getDocument("messages");
        cloudSystem.setMessage(MessageType.PROXY_JOIN_MAINTENANCE, messages.getString("player-login-not-whitelisted"));
        cloudSystem.setMessage(MessageType.LOGIN_NETWORK_FULL, messages.getString("player-login-full-server"));
        cloudSystem.setMessage(MessageType.SERVICE_STARTING, messages.getString("service-start"));
        cloudSystem.setMessage(MessageType.SERVICE_STOPPING, messages.getString("service-stop"));

        cloudSystem.getConfig().setNotifyServerUpdates(document.getBoolean("ingameServiceStartStopMessages"));

        this.fillTabPlaceholders(cloudSystem.getPlaceholders());
        this.fillLoginPlaceholders(cloudSystem.getPlaceholders());

        return ExecuteResult.success();
    }

    private MotdLayout asMotdLayout(Document document) {
        return new MotdLayout(
                document.getString("firstLine"),
                document.getString("secondLine"),
                document.getBoolean("autoSlot"),
                document.getInt("autoSlotMaxPlayersDistance"),
                document.get("playerInfo", String[].class),
                document.getString("protocolText")
        );
    }
}
