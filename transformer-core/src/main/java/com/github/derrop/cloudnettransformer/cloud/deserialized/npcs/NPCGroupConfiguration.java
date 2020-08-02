package com.github.derrop.cloudnettransformer.cloud.deserialized.npcs;

import java.util.Map;

public class NPCGroupConfiguration {

    private final String targetGroup;
    private final double infoLineDistance;
    private final int inventorySize;
    private final int startSlot;
    private final int endSlot;
    private final boolean showFullServices;
    private final NPCItem onlineItem;
    private final NPCItem emptyItem;
    private final NPCItem fullItem;
    private final Map<Integer, NPCItem> inventoryLayout;

    public NPCGroupConfiguration(String targetGroup, double infoLineDistance, int inventorySize, int startSlot, int endSlot, boolean showFullServices, NPCItem onlineItem, NPCItem emptyItem, NPCItem fullItem, Map<Integer, NPCItem> inventoryLayout) {
        this.targetGroup = targetGroup;
        this.infoLineDistance = infoLineDistance;
        this.inventorySize = inventorySize;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
        this.showFullServices = showFullServices;
        this.onlineItem = onlineItem;
        this.emptyItem = emptyItem;
        this.fullItem = fullItem;
        this.inventoryLayout = inventoryLayout;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }

    public double getInfoLineDistance() {
        return this.infoLineDistance;
    }

    public int getInventorySize() {
        return this.inventorySize;
    }

    public int getStartSlot() {
        return this.startSlot;
    }

    public int getEndSlot() {
        return this.endSlot;
    }

    public boolean isShowFullServices() {
        return this.showFullServices;
    }

    public NPCItem getOnlineItem() {
        return this.onlineItem;
    }

    public NPCItem getEmptyItem() {
        return this.emptyItem;
    }

    public NPCItem getFullItem() {
        return this.fullItem;
    }

    public Map<Integer, NPCItem> getInventoryLayout() {
        return this.inventoryLayout;
    }
}
