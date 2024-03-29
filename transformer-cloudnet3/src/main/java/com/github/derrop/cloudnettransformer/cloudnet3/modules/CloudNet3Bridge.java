package com.github.derrop.cloudnettransformer.cloudnet3.modules;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.Fallback;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.FallbackConfiguration;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
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

@DescribedCloudExecutor(name = "Bridge")
public class CloudNet3Bridge extends FileDownloader implements CloudReaderWriter {

    public CloudNet3Bridge() {
        super("Bridge", "https://cloudnetservice.eu/cloudnet/updates/versions/${VERSION}/cloudnet-bridge.jar", "modules/cloudnet-bridge.jar");
    }

    private Path config(Path directory) {
        return directory.resolve("modules").resolve("CloudNet-Bridge").resolve("config.json");
    }

    @Override
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (!super.downloadFile(cloudSystem, directory)) {
            return ExecuteResult.failed("Failed to download file to " + directory);
        }

        Map<String, String> messages = new HashMap<>();
        messages.put("command-hub-success-connect", cloudSystem.getMessage(MessageType.COMMAND_HUB_CONNECT_SUCCESS));
        messages.put("command-hub-already-in-hub", cloudSystem.getMessage(MessageType.COMMAND_HUB_ALREADY_HUB));
        messages.put("command-hub-no-server-found", cloudSystem.getMessage(MessageType.COMMAND_HUB_NO_SERVER));
        messages.put("server-join-cancel-because-maintenance", cloudSystem.getMessage(MessageType.SERVER_JOIN_MAINTENANCE));
        messages.put("server-join-cancel-because-only-proxy", cloudSystem.getMessage(MessageType.ONLY_PROXY_JOIN_KICK));
        messages.put("command-cloud-sub-command-no-permission", cloudSystem.getMessage(MessageType.COMMAND_CLOUD_NO_PERMISSION));
        messages.put("already-connected", cloudSystem.getMessage(MessageType.NETWORK_ALREADY_CONNECTED));

        Collection<Document> fallbackConfigurations = new ArrayList<>();
        for (FallbackConfiguration fallbackConfiguration : cloudSystem.getFallbackConfigurations()) {
            fallbackConfigurations.add(
                    Documents.newDocument()
                            .append("targetGroup", fallbackConfiguration.getTargetGroup())
                            .append("defaultFallbackTask", fallbackConfiguration.getDefaultFallback())
                            .append("fallbacks", fallbackConfiguration.getFallbacks())
            );
        }

        Document config = Documents.newDocument()
                .append("prefix", cloudSystem.getMessage(MessageType.GLOBAL_PREFIX))
                .append("onlyProxyProtection", true)
                .append("excludedOnlyProxyWalkableGroups", Collections.emptyList())
                .append("excludedGroups", Collections.emptyList())
                .append("bungeeFallbackConfigurations", fallbackConfigurations)
                .append("messages", messages)
                .append("logPlayerConnections", true)
                .append("properties", Documents.newDocument());

        Documents.newDocument("config", config).json().write(this.config(directory));

        return ExecuteResult.success();
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) {
        cloudSystem.addExcludedServiceFiles("cloudnet-bridge.jar");

        Path configPath = this.config(directory);
        if (Files.notExists(configPath)) {
            return ExecuteResult.failed("Config '" + configPath + "' not found");
        }

        Document document = Documents.jsonStorage().read(configPath).getDocument("config");
        if (document == null) {
            return ExecuteResult.failed("No config entry found in the Bridge config from " + configPath);
        }

        for (Document fallbackConfiguration : document.getDocuments("bungeeFallbackConfigurations")) {
            cloudSystem.getFallbackConfigurations().add(new FallbackConfiguration(
                    fallbackConfiguration.getString("targetGroup"),
                    fallbackConfiguration.getString("defaultFallbackTask"),
                    fallbackConfiguration.get("fallbacks", TypeToken.getParameterized(Collection.class, Fallback.class).getType())
            ));
        }

        cloudSystem.setMessage(MessageType.GLOBAL_PREFIX, document.getString("prefix"));

        Document messages = document.getDocument("messages");

        cloudSystem.setMessage(MessageType.COMMAND_HUB_CONNECT_SUCCESS, messages.getString("command-hub-success-connect"));
        cloudSystem.setMessage(MessageType.COMMAND_HUB_ALREADY_HUB, messages.getString("command-hub-already-in-hub"));
        cloudSystem.setMessage(MessageType.PROXY_JOIN_MAINTENANCE, messages.getString("server-join-cancel-because-maintenance"));
        cloudSystem.setMessage(MessageType.COMMAND_HUB_NO_SERVER, messages.getString("command-hub-no-server-found"));
        cloudSystem.setMessage(MessageType.ONLY_PROXY_JOIN_KICK, messages.getString("server-join-cancel-because-only-proxy"));
        cloudSystem.setMessage(MessageType.COMMAND_CLOUD_NO_PERMISSION, messages.getString("command-cloud-sub-command-no-permission"));
        cloudSystem.setMessage(MessageType.NETWORK_ALREADY_CONNECTED, messages.getString("already-connected"));

        return ExecuteResult.success();
    }
}
