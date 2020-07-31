package com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user;

public class UserGroup {

    private final String name;
    private final long timeout;

    public UserGroup(String name, long timeout) {
        this.name = name;
        this.timeout = timeout;
    }

    public String getName() {
        return this.name;
    }

    public long getTimeout() {
        return this.timeout;
    }
}
