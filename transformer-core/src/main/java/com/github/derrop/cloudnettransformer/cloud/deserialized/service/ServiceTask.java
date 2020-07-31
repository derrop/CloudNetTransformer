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
    private final int maxServices;
    private final ServiceEnvironment environment;
    private final TemplateInstallerType templateInstallerType;
    private final Document properties;

    public ServiceTask(String name,
                       Collection<ServiceTemplate> templates, Collection<ServiceInclusion> inclusions, Collection<ServiceDeployment> deployments,
                       Collection<String> jvmOptions, boolean maintenance, boolean staticServices, Collection<String> nodes,
                       Collection<String> groups, int maxMemory, int startPort, int minServices, int maxServices,
                       ServiceEnvironment environment, TemplateInstallerType templateInstallerType, Document properties) {
        super(name, templates, inclusions, deployments, jvmOptions);
        this.maintenance = maintenance;
        this.staticServices = staticServices;
        this.nodes = nodes;
        this.groups = groups;
        this.maxMemory = maxMemory;
        this.startPort = startPort;
        this.minServices = minServices;
        this.maxServices = maxServices;
        this.environment = environment;
        this.templateInstallerType = templateInstallerType;
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

    public int getMaxServices() {
        return this.maxServices;
    }

    public ServiceEnvironment getEnvironment() {
        return this.environment;
    }

    public TemplateInstallerType getTemplateInstallerType() {
        return this.templateInstallerType;
    }

    public Document getProperties() {
        return this.properties;
    }
}
