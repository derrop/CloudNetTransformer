package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.Fallback;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.FallbackConfiguration;
import com.github.derrop.cloudnettransformer.cloud.reader.CloudReader;
import com.github.derrop.cloudnettransformer.cloud.writer.CloudWriter;
import com.github.derrop.cloudnettransformer.cloud.writer.FileDownloader;
import com.github.derrop.cloudnettransformer.document.Document;
import com.github.derrop.cloudnettransformer.document.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CloudNet3Bridge extends FileDownloader implements CloudReader, CloudWriter {

    public CloudNet3Bridge() {
        super("Bridge", "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet-v3/job/master/lastSuccessfulBuild/artifact/cloudnet-modules/cloudnet-bridge/build/libs/cloudnet-bridge.jar", "modules/cloudnet-bridge.jar");
    }

    private Path config(Path directory) {
        return directory.resolve("modules").resolve("CloudNet-Bridge").resolve("config.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (!super.write(cloudSystem, directory)) {
            return false;
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

        Documents.jsonStorage().write(Documents.newDocument("config", config), this.config(directory));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {
        Path configPath = this.config(directory);
        if (!Files.exists(configPath)) {
            return false;
        }

        Document document = Documents.jsonStorage().read(configPath).getDocument("config");
        if (document == null) {
            return false;
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

        return true;
    }
}
