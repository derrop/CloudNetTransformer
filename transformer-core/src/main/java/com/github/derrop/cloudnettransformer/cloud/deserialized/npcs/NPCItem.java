package com.github.derrop.cloudnettransformer.cloud.deserialized.npcs;

public class NPCItem {

    private final String material;
    private final byte subId;
    private final String displayName;
    private final String[] lore;

    public NPCItem(String material, byte subId, String displayName, String[] lore) {
        this.material = material;
        this.subId = subId;
        this.displayName = displayName;
        this.lore = lore;
    }

    public String getMaterial() {
        return this.material;
    }

    public byte getSubId() {
        return this.subId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String[] getLore() {
        return this.lore;
    }
}
