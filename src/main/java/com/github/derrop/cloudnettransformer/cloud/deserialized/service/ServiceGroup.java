package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

import java.util.Collection;

public class ServiceGroup extends ServiceConfigurationBase {

    private final Collection<ServiceEnvironment> environments;

    public ServiceGroup(String name, Collection<ServiceTemplate> templates, Collection<ServiceInclusion> inclusions, Collection<ServiceDeployment> deployments, Collection<String> jvmOptions, Collection<ServiceEnvironment> environments) {
        super(name, templates, inclusions, deployments, jvmOptions);
        this.environments = environments;
    }

    public Collection<ServiceEnvironment> getEnvironments() {
        return this.environments;
    }
}
