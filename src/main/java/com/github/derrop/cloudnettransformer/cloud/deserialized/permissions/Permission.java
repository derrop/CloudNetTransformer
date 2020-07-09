package com.github.derrop.cloudnettransformer.cloud.deserialized.permissions;

public class Permission {

    private final String name;
    private final int potency;
    private final long timeout;
    private final String targetGroup;

    public Permission(String name, int potency, long timeout, String targetGroup) {
        this.name = name;
        this.potency = potency;
        this.timeout = timeout;
        this.targetGroup = targetGroup;
    }

    public String getName() {
        return this.name;
    }

    public int getPotency() {
        return this.potency;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }
}
