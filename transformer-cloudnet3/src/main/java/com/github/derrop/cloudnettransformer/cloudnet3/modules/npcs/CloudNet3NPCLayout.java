package com.github.derrop.cloudnettransformer.cloudnet3.modules.npcs;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.PlaceholderCategory;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.PlaceholderType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.NPCConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.NPCGroupConfiguration;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.FileDownloader;
import com.github.derrop.cloudnettransformer.cloudnet3.CloudNet3Utils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@DescribedCloudExecutor(name = "NPC Layout")
public class CloudNet3NPCLayout extends FileDownloader implements CloudReaderWriter {
    public CloudNet3NPCLayout() {
        super("https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet-v3/job/master/lastSuccessfulBuild/artifact/cloudnet-modules/cloudnet-npcs/build/libs/cloudnet-npcs.jar", "modules/cloudnet-npcs.jar");
    }

    private Path config(Path directory) {
        return directory.resolve("modules").resolve("CloudNet-NPCs").resolve("config.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (cloudSystem.getNpcConfiguration() == null) {
            return false;
        }
        if (!super.downloadFile(directory)) {
            return false;
        }

        NPCConfiguration npcConfiguration = cloudSystem.getNpcConfiguration();

        Document config = Documents.newDocument();

        Collection<Document> configurations = new ArrayList<>();

        for (NPCGroupConfiguration configuration : npcConfiguration.getConfigurations()) {
            Document document = Documents.newDocument(configuration);
            document.append("npcTabListRemoveTicks", 40);

            for (String key : new String[]{"onlineItem", "emptyItem", "fullItem"}) {
                Document item = document.getDocument(key);
                this.updateItemPlaceholders(cloudSystem, item);
                document.append(key, item);
            }

            configurations.add(document);
        }

        config.append("configurations", configurations);

        Map<String, String> messages = new HashMap<>();
        messages.put("command-create-display-name-too-long", cloudSystem.getMessage(MessageType.NPC_CREATE_DISPLAY_TOO_LONG));
        messages.put("command-edit-success", cloudSystem.getMessage(MessageType.NPC_EDIT_SUCCESS));
        messages.put("command-cleanup-success", cloudSystem.getMessage(MessageType.NPC_CLEANUP_SUCCESS));
        messages.put("command-create-texture-fetch-fail", cloudSystem.getMessage(MessageType.NPC_CREATE_TEXTURE_FETCH_FAIL));
        messages.put("command-remove-success", cloudSystem.getMessage(MessageType.NPC_REMOVE_SUCCESS));
        messages.put("command-no-npc-in-range", cloudSystem.getMessage(MessageType.NPC_NO_NPC_IN_RANGE));
        messages.put("command-edit-invalid-action", cloudSystem.getMessage(MessageType.NPC_EDIT_INVALID_ACTION));
        messages.put("command-create-invalid-material", cloudSystem.getMessage(MessageType.NPC_CREATE_INVALID_MATERIAL));
        messages.put("command-create-success", cloudSystem.getMessage(MessageType.NPC_CREATE_SUCCESS));
        config.append("messages", messages);

        Documents.newDocument().append("config", config).json().write(this.config(directory));

        return true;
    }

    private void updateItemPlaceholders(CloudSystem cloudSystem, Document item) {
        String displayName = item.getString("displayName");
        String[] lore = item.get("lore", String[].class);

        Map<PlaceholderType, String> placeholders = new HashMap<>();
        CloudNet3Utils.fillServiceInfoPlaceholders(PlaceholderCategory.NPC_INVENTORY, placeholders);

        displayName = cloudSystem.updatePlaceholders(displayName, placeholders);
        for (int i = 0; i < lore.length; i++) {
            lore[i] = cloudSystem.updatePlaceholders(lore[i], placeholders);
        }

        item.append("displayName", displayName).append("lore", lore);
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        cloudSystem.addExcludedServiceFiles("cloudnet-npcs.jar");

        Path configPath = this.config(directory);
        if (Files.notExists(configPath)) {
            return false;
        }

        Document config = Documents.jsonStorage().read(configPath).getDocument("config");
        if (config == null || !config.contains("configurations")) {
            return false;
        }

        Collection<NPCGroupConfiguration> configurations = config.get("configurations", TypeToken.getParameterized(Collection.class, NPCGroupConfiguration.class).getType());
        cloudSystem.setNpcConfiguration(new NPCConfiguration(configurations));

        CloudNet3Utils.fillServiceInfoPlaceholders(PlaceholderCategory.NPC_INVENTORY, cloudSystem.getPlaceholders());

        return true;
    }
}
