package com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd;

public class MotdLayout {

    private final String firstLine;
    private final String secondLine;
    private final boolean autoSlot;
    private final int autoSlotDistance;
    private final String[] playerInfo;
    private final String protocolText;

    public MotdLayout(String firstLine, String secondLine, boolean autoSlot, int autoSlotDistance, String[] playerInfo, String protocolText) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.autoSlot = autoSlot;
        this.autoSlotDistance = autoSlotDistance;
        this.playerInfo = playerInfo;
        this.protocolText = protocolText;
    }

    public String getFirstLine() {
        return this.firstLine;
    }

    public String getSecondLine() {
        return this.secondLine;
    }

    public boolean isAutoSlot() {
        return this.autoSlot;
    }

    public int getAutoSlotDistance() {
        return this.autoSlotDistance;
    }

    public String[] getPlayerInfo() {
        return this.playerInfo;
    }

    public String getProtocolText() {
        return this.protocolText;
    }
}
