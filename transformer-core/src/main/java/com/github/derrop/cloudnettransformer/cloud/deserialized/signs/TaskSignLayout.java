package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

public class TaskSignLayout {

    private final String task;
    private final SignLayout onlineLayout;
    private final SignLayout emptyLayout;
    private final SignLayout maintenanceLayout;
    private final SignLayout fullLayout;

    public TaskSignLayout(String task, SignLayout onlineLayout, SignLayout emptyLayout, SignLayout maintenanceLayout, SignLayout fullLayout) {
        this.task = task;
        this.onlineLayout = onlineLayout;
        this.emptyLayout = emptyLayout;
        this.maintenanceLayout = maintenanceLayout;
        this.fullLayout = fullLayout;
    }

    public String getTask() {
        return this.task;
    }

    public SignLayout getOnlineLayout() {
        return this.onlineLayout;
    }

    public SignLayout getEmptyLayout() {
        return this.emptyLayout;
    }

    public SignLayout getMaintenanceLayout() {
        return this.maintenanceLayout;
    }

    public SignLayout getFullLayout() {
        return this.fullLayout;
    }
}
