package com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist;

import java.util.Collection;

public class TabListConfiguration {

    private final String targetGroup;
    private final Collection<TabList> entries;
    private final double animationsPerSecond;

    public TabListConfiguration(String targetGroup, Collection<TabList> entries, double animationsPerSecond) {
        this.targetGroup = targetGroup;
        this.entries = entries;
        this.animationsPerSecond = animationsPerSecond;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }

    public Collection<TabList> getEntries() {
        return this.entries;
    }

    public double getAnimationsPerSecond() {
        return this.animationsPerSecond;
    }
}
