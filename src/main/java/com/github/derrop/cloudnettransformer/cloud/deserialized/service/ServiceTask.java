package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

import com.github.derrop.documents.Document;

import java.util.Collection;

public class ServiceTask extends ServiceConfigurationBase {

    private final boolean maintenance;
    private final boolean staticServices;
    private final Collection<String> nodes;
    private final Collection<String> groups;
    private final int maxMemory;
    private final int startPort;
    private final int minServices;
    private final ServiceEnvironment environment;
    private final Document properties;

    public ServiceTask(String name,
                       Collection<ServiceTemplate> templates, Collection<ServiceInclusion> inclusions, Collection<ServiceDeployment> deployments,
                       Collection<String> jvmOptions, boolean maintenance, boolean staticServices,
                       Collection<String> nodes, Collection<String> groups,
                       int maxMemory, int startPort, int minServices,
                       ServiceEnvironment environment, Document properties) {
        super(name, templates, inclusions, deployments, jvmOptions);
        this.maintenance = maintenance;
        this.staticServices = staticServices;
        this.nodes = nodes;
        this.groups = groups;
        this.maxMemory = maxMemory;
        this.startPort = startPort;
        this.minServices = minServices;
        this.environment = environment;
        this.properties = properties;
    }

    public boolean isMaintenance() {
        return this.maintenance;
    }

    public boolean isStaticServices() {
        return this.staticServices;
    }

    public Collection<String> getNodes() {
        return this.nodes;
    }

    public Collection<String> getGroups() {
        return this.groups;
    }

    public int getMaxMemory() {
        return this.maxMemory;
    }

    public int getStartPort() {
        return this.startPort;
    }

    public int getMinServices() {
        return this.minServices;
    }

    public ServiceEnvironment getEnvironment() {
        return this.environment;
    }

    public Document getProperties() {
        return this.properties;
    }
}
