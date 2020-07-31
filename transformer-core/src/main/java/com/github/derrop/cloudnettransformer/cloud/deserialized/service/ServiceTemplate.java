package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

public class ServiceTemplate {

    private final String storage;
    private final String prefix;
    private final String name;

    public ServiceTemplate(String storage, String prefix, String name) {
        this.storage = storage;
        this.prefix = prefix;
        this.name = name;
    }

    public String getStorage() {
        return this.storage;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getName() {
        return this.name;
    }

    public boolean isGlobal() {
        return this.prefix.equalsIgnoreCase("global");
    }

}
