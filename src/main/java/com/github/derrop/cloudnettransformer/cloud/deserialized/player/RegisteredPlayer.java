package com.github.derrop.cloudnettransformer.cloud.deserialized.player;

import java.util.UUID;

public class RegisteredPlayer {

    private final UUID uniqueId;
    private final String name;
    private final String xboxId;
    private final long firstLogin;
    private final long lastLogin;
    private final int lastVersion;
    private final String lastHost;
    private final int lastPort;

    public RegisteredPlayer(UUID uniqueId, String name, String xboxId, long firstLogin, long lastLogin, int lastVersion, String lastHost, int lastPort) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.xboxId = xboxId;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
        this.lastVersion = lastVersion;
        this.lastHost = lastHost;
        this.lastPort = lastPort;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }

    public String getXboxId() {
        return this.xboxId;
    }

    public long getFirstLogin() {
        return this.firstLogin;
    }

    public long getLastLogin() {
        return this.lastLogin;
    }

    public int getLastVersion() {
        return this.lastVersion;
    }

    public String getLastHost() {
        return this.lastHost;
    }

    public int getLastPort() {
        return this.lastPort;
    }

}
