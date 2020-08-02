package com.github.derrop.cloudnettransformer.cloudnet3.modules.npcs;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.NPCAction;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.PlacedNPC;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.ProfileProperty;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "NPCs")
public class CloudNet3NPCs implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloudnet_module_configuration";
    private static final String DOCUMENT_NAME = "npc_store";

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);

        database.insert(DOCUMENT_NAME, Documents.newDocument("npcs", cloudSystem.getNpcs().stream()
                .map(npc -> Documents.newDocument()
                        .append("uuid", npc.getUniqueId())
                        .append("displayName", npc.getDisplayName())
                        .append("infoLine", npc.getInfoLine())
                        .append("profileProperties", npc.getProfileProperties())
                        .append("position", Documents.newDocument()
                                .append("x", npc.getX())
                                .append("y", npc.getY())
                                .append("z", npc.getZ())
                                .append("yaw", npc.getYaw())
                                .append("pitch", npc.getPitch())
                                .append("world", npc.getWorld())
                                .append("group", npc.getPlacedGroup())
                        )
                        .append("targetGroup", npc.getTargetGroup())
                        .append("itemInHand", npc.getItemInHand())
                        .append("lookAtPlayer", npc.isLookAtPlayer())
                        .append("imitatePlayer", npc.isImitatePlayer())
                        .append("rightClickAction", npc.getRightClickAction())
                        .append("leftClickAction", npc.getLeftClickAction())
                )
                .collect(Collectors.toList()))
        );

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        Document document = database.get(DOCUMENT_NAME);
        if (document == null) {
            return true;
        }

        for (Document npc : document.getDocuments("npcs")) {
            Document position = npc.getDocument("position");
            if (position == null) {
                continue;
            }
            cloudSystem.getNpcs().add(new PlacedNPC(
                    npc.get("uuid", UUID.class),
                    npc.getString("displayName"),
                    npc.getString("infoLine"),
                    npc.get("profileProperties", TypeToken.getParameterized(Collection.class, ProfileProperty.class).getType()),
                    npc.getString("itemInHand"),
                    npc.getString("targetGroup"),
                    position.getString("group"),
                    position.getString("world"),
                    position.getDouble("x"),
                    position.getDouble("y"),
                    position.getDouble("z"),
                    position.getFloat("yaw"),
                    position.getFloat("pitch"),
                    npc.getBoolean("lookAtPlayer"),
                    npc.getBoolean("imitatePlayer"),
                    npc.get("rightClickAction", NPCAction.class),
                    npc.get("leftClickAction", NPCAction.class)
            ));
        }

        return true;
    }
}
