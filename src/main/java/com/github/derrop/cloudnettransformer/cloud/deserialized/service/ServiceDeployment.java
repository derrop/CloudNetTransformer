package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

public class ServiceDeployment {

    private final ServiceTemplate template;
    private final String[] excludes;

    public ServiceDeployment(ServiceTemplate template, String[] excludes) {
        this.template = template;
        this.excludes = excludes;
    }

    public ServiceTemplate getTemplate() {
        return this.template;
    }

    public String[] getExcludes() {
        return this.excludes;
    }
}
