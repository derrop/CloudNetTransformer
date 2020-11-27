package com.github.derrop.cloudnettransformer.cloudnet2.mobs;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.PlaceholderType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.NPCAction;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.PlacedNPC;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.ProfileProperty;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
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
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        Map<PlaceholderType, String> placeholders = new HashMap<>();
        this.fillInfoLinePlaceholders(placeholders);

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
                        .append("displayMessage", cloudSystem.updatePlaceholders(npc.getInfoLine(), placeholders))
                        .append("metaDataDoc", Documents.newDocument().append("dataCatcher", Documents.newDocument()))));

        database.insert(DOCUMENT_NAME, Documents.newDocument("mobs", mobs));

        return ExecuteResult.success();
    }

    private void fillInfoLinePlaceholders(Map<PlaceholderType, String> map) {
        map.put(PlaceholderType.NPCS_INFO_MAX_PLAYERS, "%max_players%");
        map.put(PlaceholderType.NPCS_INFO_GROUP, "%group%");
        map.put(PlaceholderType.NPCS_INFO_ONLINE_PLAYERS, "%group_online%");
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        Document document = database.get(DOCUMENT_NAME);
        if (document == null) {
            return ExecuteResult.failed("No " + DOCUMENT_NAME + " in " + DATABASE_NAME + " found");
        }
        Document mobs = document.getDocument("mobs");
        if (mobs == null) {
            return ExecuteResult.failed("No mobs entry in " + DATABASE_NAME + ":" + DOCUMENT_NAME + " found");
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
                    Collections.singletonList(new ProfileProperty(
                            "textures",
                            "ewogICJ0aW1lc3RhbXAiIDogMTU5NjQwOTAyNDU3NiwKICAicHJvZmlsZUlkIiA6ICIyNjRmMGVlMzY0NmU0NDc1OWE4NmU5ZjY3NjE2N2Y1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJKcmFtZWUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM2ZjgxYWYzMWUwZGNkNGZlZTRiYmIwNzk3ZGUwYzBhOTU0OTlhZjJlMDNhMzY0MjQ4YTQwNTIwMTdiM2U2ZCIKICAgIH0KICB9Cn0=",
                            "B05ubm5ysH4EyHhIMlrD2esNbgVDX6tDww5pFLBNQI/cs6jOIhuAUBskIQEXyv1aeoWEPNhAZjcZCOj/9wZBRhqDy0Ulj++DFQTj7LkO2lR5E+BvydalRFwnMQvcpuySGvb9fn1I1oaVDJyxWtpBKQ1cQ5iVewpvLrd0pTu+RyFrhnoTu18BN1hx/xDgpAt3iFauY7uwiD1m0KRii1CB415X9Bil5ivv/7KhxAVDlCPpF1oTQ6iCf8uEw1QFw+R+Vv/1wb/EPWGwxWEAnEm5kv9NhzdOaz+WXMxpiAwPGvFum5uRYtL3byleUcqKTSFbsnFHPc/Yb4lNeaX+kHPeUzwYBZ2EtGZxtO8ewoPtEWGAt585ZUsLG4QfhTSXt+2SLYOVGsZhTi59P8aWXxmQHVxmNLLKgB7QQ5uvMTPYYZy3zlllKQYefT1KQMtKTmRDhjqYEe9Mm1a7Le0K33ArtWpcX8Pgqda7Jf2mbJo9tT0Q3BF+P5fqf1p1fB4VO3dS3oDA9fHih3TzbY1+M/0ikwfVjJXsIq/URlhw+ayZPUE52duJuX9PyT/tjT1WdaLzsMKVWF3K8/9mXviMjA2qSLdWiJXybpceksfd3AD7a+YBuakl4r0t2KraGKtRXfWp7GCJ5zzTZovLrRe3n28P4/5SXHhphZShmurP1FpCUIw="
                    )),
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

        this.fillInfoLinePlaceholders(cloudSystem.getPlaceholders());

        return ExecuteResult.success();
    }
}
