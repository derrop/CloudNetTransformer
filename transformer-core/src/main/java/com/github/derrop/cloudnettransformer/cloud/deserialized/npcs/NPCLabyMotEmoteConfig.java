package com.github.derrop.cloudnettransformer.cloud.deserialized.npcs;

public class NPCLabyMotEmoteConfig {

    private final int[] emoteIds;
    private final int[] onJoinEmoteIds;
    private final int minEmoteDelayTicks;
    private final int maxEmoteDelayTicks;
    private final boolean playEmotesSynchronous;

    public NPCLabyMotEmoteConfig(int[] emoteIds, int[] onJoinEmoteIds, int minEmoteDelayTicks, int maxEmoteDelayTicks, boolean playEmotesSynchronous) {
        this.emoteIds = emoteIds;
        this.onJoinEmoteIds = onJoinEmoteIds;
        this.minEmoteDelayTicks = minEmoteDelayTicks;
        this.maxEmoteDelayTicks = maxEmoteDelayTicks;
        this.playEmotesSynchronous = playEmotesSynchronous;
    }

    public int[] getEmoteIds() {
        return this.emoteIds;
    }

    public int[] getOnJoinEmoteIds() {
        return this.onJoinEmoteIds;
    }

    public int getMinEmoteDelayTicks() {
        return this.minEmoteDelayTicks;
    }

    public int getMaxEmoteDelayTicks() {
        return this.maxEmoteDelayTicks;
    }

    public boolean isPlayEmotesSynchronous() {
        return this.playEmotesSynchronous;
    }
}
