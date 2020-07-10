package com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback;

public class Fallback {

    private final String task;
    private final String permission;
    private final int priority;

    public Fallback(String task, String permission, int priority) {
        this.task = task;
        this.permission = permission;
        this.priority = priority;
    }

    public String getTask() {
        return this.task;
    }

    public String getPermission() {
        return this.permission;
    }

    public int getPriority() {
        return this.priority;
    }
}
