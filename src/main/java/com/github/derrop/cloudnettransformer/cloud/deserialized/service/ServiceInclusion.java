package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

public class ServiceInclusion {

    private final String target;
    private final String url;

    public ServiceInclusion(String target, String url) {
        this.target = target;
        this.url = url;
    }

    public String getTarget() {
        return this.target;
    }

    public String getUrl() {
        return this.url;
    }
}
