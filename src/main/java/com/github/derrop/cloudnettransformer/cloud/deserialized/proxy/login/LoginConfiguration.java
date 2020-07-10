package com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.login;

import java.util.Collection;

public class LoginConfiguration {

    private final String targetGroup;
    private final boolean maintenance;
    private final int maxPlayers;
    private final Collection<String> whitelist;

    public LoginConfiguration(String targetGroup, boolean maintenance, int maxPlayers, Collection<String> whitelist) {
        this.targetGroup = targetGroup;
        this.maintenance = maintenance;
        this.maxPlayers = maxPlayers;
        this.whitelist = whitelist;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }

    public boolean isMaintenance() {
        return this.maintenance;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public Collection<String> getWhitelist() {
        return this.whitelist;
    }
}
