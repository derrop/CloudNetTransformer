package com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed;

import java.util.Collection;
import java.util.UUID;

public class PlacedNPC {

    private final UUID uniqueId;
    private final String displayName;
    private final String infoLine;
    private final Collection<ProfileProperty> profileProperties;
    private final String itemInHand;
    private final String targetGroup;
    private final String placedGroup;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    private final boolean lookAtPlayer;
    private final boolean imitatePlayer;
    private final NPCAction rightClickAction;
    private final NPCAction leftClickAction;

    public PlacedNPC(UUID uniqueId, String displayName, String infoLine, Collection<ProfileProperty> profileProperties,
                     String itemInHand, String targetGroup, String placedGroup, String world,
                     double x, double y, double z, float yaw, float pitch, boolean lookAtPlayer, boolean imitatePlayer,
                     NPCAction rightClickAction, NPCAction leftClickAction) {
        this.uniqueId = uniqueId;
        this.displayName = displayName;
        this.infoLine = infoLine;
        this.profileProperties = profileProperties;
        this.itemInHand = itemInHand;
        this.targetGroup = targetGroup;
        this.placedGroup = placedGroup;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.lookAtPlayer = lookAtPlayer;
        this.imitatePlayer = imitatePlayer;
        this.rightClickAction = rightClickAction;
        this.leftClickAction = leftClickAction;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getInfoLine() {
        return this.infoLine;
    }

    public Collection<ProfileProperty> getProfileProperties() {
        return this.profileProperties;
    }

    public String getItemInHand() {
        return this.itemInHand;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }

    public String getPlacedGroup() {
        return this.placedGroup;
    }

    public String getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isLookAtPlayer() {
        return this.lookAtPlayer;
    }

    public boolean isImitatePlayer() {
        return this.imitatePlayer;
    }

    public NPCAction getRightClickAction() {
        return this.rightClickAction;
    }

    public NPCAction getLeftClickAction() {
        return this.leftClickAction;
    }
}
