package com.github.derrop.cloudnettransformer.cloudnet2.mobs;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.NPCConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.NPCGroupConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.NPCItem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceGroup;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "MobLayout")
public class CloudNet2MobLayout implements CloudReaderWriter {

    private Path config(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("local").resolve("servermob_config.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (cloudSystem.getNpcConfiguration() == null || cloudSystem.getNpcConfiguration().getConfigurations().isEmpty()) {
            return false;
        }

        NPCGroupConfiguration configuration = cloudSystem.getNpcConfiguration().getConfigurations().iterator().next();
        Document config = Documents.newDocument();

        config
                .append("inventorySize", configuration.getInventorySize())
                .append("startPoint", configuration.getStartSlot())
                .append("itemLayout", this.asJson(configuration.getOnlineItem()))
                .append("defaultItemInventory", configuration.getInventoryLayout().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> this.asJson(entry.getValue()))));

        Documents.newDocument().append("mobConfig", config).json().write(this.config(directory));

        return true;
    }

    private Document asJson(NPCItem item) {
        return Documents.newDocument()
                .append("itemId", 0)
                .append("itemName", item.getMaterial())
                .append("subId", item.getSubId())
                .append("display", item.getDisplayName())
                .append("lore", item.getLore());
    }

    private NPCItem asItem(Document document) {
        return new NPCItem(
                document.getString("itemName"),
                document.getByte("subId"),
                document.getString("display"),
                document.get("lore", String[].class)
        );
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        Path configPath = this.config(directory);
        if (Files.notExists(configPath)) {
            return false;
        }

        Document config = Documents.jsonStorage().read(configPath).getDocument("mobConfig");
        if (config == null) {
            return false;
        }

        Collection<NPCGroupConfiguration> configurations = new ArrayList<>();

        for (ServiceGroup group : cloudSystem.getGroups()) {
            if (group.isSupportingSigns()) {
                NPCItem itemLayout = this.asItem(config.getDocument("itemLayout"));
                Document inventoryDocument = config.getDocument("defaultItemInventory");
                Map<Integer, NPCItem> inventory = new HashMap<>();
                for (String key : inventoryDocument.keys()) {
                    inventory.put(Integer.parseInt(key), this.asItem(inventoryDocument.getDocument(key)));
                }

                configurations.add(new NPCGroupConfiguration(
                        group.getName(),
                        0.15,
                        config.getInt("inventorySize"),
                        config.getInt("startPoint"),
                        config.getInt("inventorySize"),
                        true,
                        itemLayout, itemLayout, itemLayout,
                        inventory
                ));
            }
        }

        cloudSystem.setNpcConfiguration(new NPCConfiguration(configurations));

        return true;
    }
}
