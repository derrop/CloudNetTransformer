package com.github.derrop.cloudnettransformer.cloud.deserialized.npcs;

import java.util.Collection;

public class NPCConfiguration {

    private final Collection<NPCGroupConfiguration> configurations;

    public NPCConfiguration(Collection<NPCGroupConfiguration> configurations) {
        this.configurations = configurations;
    }

    public Collection<NPCGroupConfiguration> getConfigurations() {
        return this.configurations;
    }
}
