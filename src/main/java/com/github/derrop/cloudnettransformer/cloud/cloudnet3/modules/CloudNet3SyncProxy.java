package com.github.derrop.cloudnettransformer.cloud.cloudnet3.modules;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.login.LoginConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdLayout;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist.TabListConfiguration;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.FileDownloader;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
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
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (!super.downloadFile(directory)) {
            return false;
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
                                                .append("motds", motdConfiguration.getMotdLayouts().stream().map(this::asJson).collect(Collectors.toList()))
                                                .append("maintenanceMotds", motdConfiguration.getMaintenanceLayouts().stream().map(this::asJson).collect(Collectors.toList()));
                                    } else {
                                        loginDoc.append("motds", Collections.emptyList()).append("maintenanceMotds", Collections.emptyList());
                                    }

                                    return loginDoc;
                                }).collect(Collectors.toList())
                )
                .append("tabListConfigurations", cloudSystem.getTabListConfigurations())
                .append("ingameServiceStartStopMessages", cloudSystem.getConfig().shouldNotifyServerUpdates());

        Documents.jsonStorage().write(Documents.newDocument().append("config", document), this.config(directory));

        return true;
    }

    private Document asJson(MotdLayout layout) {
        return Documents.newDocument()
                .append("firstLine", layout.getFirstLine())
                .append("secondLine", layout.getSecondLine())
                .append("autoSlot", layout.isAutoSlot())
                .append("autoSlotMaxPlayersDistance", layout.getAutoSlotDistance())
                .append("playerInfo", layout.getPlayerInfo())
                .append("protocolText", layout.getProtocolText());
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {
        cloudSystem.addExcludedServiceFiles("cloudnet-syncproxy.jar");

        Path configPath = this.config(directory);
        if (!Files.exists(configPath)) {
            return false;
        }
        Document document = Documents.jsonStorage().read(configPath).getDocument("config");
        if (document == null) {
            return false;
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

        return true;
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
