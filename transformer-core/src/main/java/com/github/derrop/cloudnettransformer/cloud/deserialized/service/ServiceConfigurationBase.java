package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

import java.util.Collection;

public class ServiceConfigurationBase {

    private final String name;
    private final Collection<ServiceTemplate> templates;
    private final Collection<ServiceInclusion> inclusions;
    private final Collection<ServiceDeployment> deployments;
    private final Collection<String> jvmOptions;

    public ServiceConfigurationBase(String name, Collection<ServiceTemplate> templates, Collection<ServiceInclusion> inclusions, Collection<ServiceDeployment> deployments, Collection<String> jvmOptions) {
        this.name = name;
        this.templates = templates;
        this.inclusions = inclusions;
        this.deployments = deployments;
        this.jvmOptions = jvmOptions;
    }

    public String getName() {
        return this.name;
    }

    public Collection<ServiceTemplate> getTemplates() {
        return this.templates;
    }

    public Collection<ServiceInclusion> getInclusions() {
        return this.inclusions;
    }

    public Collection<ServiceDeployment> getDeployments() {
        return this.deployments;
    }

    public Collection<String> getJvmOptions() {
        return this.jvmOptions;
    }
}
