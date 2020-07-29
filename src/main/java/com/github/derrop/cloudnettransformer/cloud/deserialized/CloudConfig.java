package com.github.derrop.cloudnettransformer.cloud.deserialized;

public class CloudConfig {

    private String componentName;
    private boolean notifyServerUpdates;
    private String ip;
    private int mainPort;
    private double maxCPUUsageToStartServices;
    private String jvmCommand;
    private int maxMemory;

    public CloudConfig(String componentName, boolean notifyServerUpdates, String ip, int mainPort, double maxCPUUsageToStartServices, String jvmCommand, int maxMemory) {
        this.componentName = componentName;
        this.notifyServerUpdates = notifyServerUpdates;
        this.ip = ip;
        this.mainPort = mainPort;
        this.maxCPUUsageToStartServices = maxCPUUsageToStartServices;
        this.jvmCommand = jvmCommand;
        this.maxMemory = maxMemory;
    }

    public String getComponentName() {
        return this.componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public boolean shouldNotifyServerUpdates() {
        return this.notifyServerUpdates;
    }

    public void setNotifyServerUpdates(boolean notifyServerUpdates) {
        this.notifyServerUpdates = notifyServerUpdates;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getMainPort() {
        return this.mainPort;
    }

    public void setMainPort(int mainPort) {
        this.mainPort = mainPort;
    }

    public double getMaxCPUUsageToStartServices() {
        return this.maxCPUUsageToStartServices;
    }

    public void setMaxCPUUsageToStartServices(double maxCPUUsageToStartServices) {
        this.maxCPUUsageToStartServices = maxCPUUsageToStartServices;
    }

    public String getJvmCommand() {
        return this.jvmCommand;
    }

    public void setJvmCommand(String jvmCommand) {
        this.jvmCommand = jvmCommand;
    }

    public int getMaxMemory() {
        return this.maxMemory;
    }

    public void setMaxMemory(int maxMemory) {
        this.maxMemory = maxMemory;
    }
}
