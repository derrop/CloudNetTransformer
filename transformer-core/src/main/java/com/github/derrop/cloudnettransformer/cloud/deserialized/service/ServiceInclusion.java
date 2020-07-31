package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceInclusion {

    private final String target;
    private final String url;
    private final Map<String, String> headers;

    public ServiceInclusion(String target, String url) {
        this.target = target;
        this.url = url;
        this.headers = new HashMap<>();
    }

    public String getTarget() {
        return this.target;
    }

    public String getUrl() {
        return this.url;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
