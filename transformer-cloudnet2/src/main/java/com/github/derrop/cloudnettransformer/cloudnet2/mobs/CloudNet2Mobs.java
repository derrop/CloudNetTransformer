package com.github.derrop.cloudnettransformer.cloudnet2.mobs;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.NPCAction;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.PlacedNPC;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "NPCs")
public class CloudNet2Mobs implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloud_internal_cfg";
    private static final String DOCUMENT_NAME = "server_selector_mobs";

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);

        Map<UUID, Document> mobs = cloudSystem.getNpcs().stream()
                .collect(Collectors.toMap(PlacedNPC::getUniqueId, npc -> Documents.newDocument()
                        .append("uniqueId", npc.getUniqueId())
                        .append("display", npc.getDisplayName())
                        .append("name", npc.getUniqueId().toString().split("-")[0])
                        .append("type", "ZOMBIE")
                        .append("targetGroup", npc.getTargetGroup())
                        .append("itemId", -1)
                        .append("itemName", npc.getItemInHand())
                        .append("autoJoin", npc.getRightClickAction().isDirect() || npc.getLeftClickAction().isDirect())
                        .append("position", Documents.newDocument()
                                .append("group", npc.getPlacedGroup())
                                .append("world", npc.getWorld())
                                .append("x", npc.getX())
                                .append("y", npc.getY())
                                .append("z", npc.getZ())
                                .append("yaw", npc.getYaw())
                                .append("pitch", npc.getPitch())
                        )
                        .append("displayMessage", npc.getInfoLine())
                        .append("metaDataDoc", Documents.newDocument().append("dataCatcher", Documents.newDocument()))));

        database.insert(DOCUMENT_NAME, Documents.newDocument("mobs", mobs));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        Document document = database.get(DOCUMENT_NAME);
        if (document == null) {
            return false;
        }
        Document mobs = document.getDocument("mobs");
        if (mobs == null) {
            return false;
        }

        mobs.keys().stream().map(mobs::getDocument).forEach(mob -> {
            Document position = mob.getDocument("position");
            if (position == null) {
                return;
            }
            cloudSystem.getNpcs().add(new PlacedNPC(
                    new UUID(mob.get("uniqueId", UUID.class).getMostSignificantBits(), 0),
                    mob.getString("display"),
                    mob.getString("displayMessage"),
                    Collections.emptyList(), // TODO add default skin
                    mob.getString("itemName"),
                    mob.getString("targetGroup"),
                    position.getString("group"),
                    position.getString("world"),
                    position.getDouble("x"),
                    position.getDouble("y"),
                    position.getDouble("z"),
                    position.getFloat("yaw"),
                    position.getFloat("pitch"),
                    false,
                    false,
                    NPCAction.OPEN_INVENTORY,
                    NPCAction.NOTHING
            ));
        });

        return true;
    }
}
