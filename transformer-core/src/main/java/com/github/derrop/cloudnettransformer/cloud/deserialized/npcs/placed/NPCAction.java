package com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed;

public enum NPCAction {

    OPEN_INVENTORY(false),
    DIRECT_CONNECT_HIGHEST_PLAYERS(true),
    DIRECT_CONNECT_LOWEST_PLAYERS(true),
    DIRECT_CONNECT_RANDOM(true),
    NOTHING(false);

    private final boolean direct;

    NPCAction(boolean direct) {
        this.direct = direct;
    }

    public boolean isDirect() {
        return this.direct;
    }
}
