package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

public class SignLayout {

    private final String[] lines;
    private final String blockType;
    private final short subId;

    public SignLayout(String[] lines, String blockType, short subId) {
        this.lines = lines;
        this.blockType = blockType;
        this.subId = subId;
    }

    public String[] getLines() {
        return this.lines;
    }

    public String getBlockType() {
        return this.blockType;
    }

    public short getSubId() {
        return this.subId;
    }
}
