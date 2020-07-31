package com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback;

import java.util.Collection;

public class FallbackConfiguration {

    private final String targetGroup;
    private final String defaultFallback;
    private final Collection<Fallback> fallbacks;

    public FallbackConfiguration(String targetGroup, String defaultFallback, Collection<Fallback> fallbacks) {
        this.targetGroup = targetGroup;
        this.defaultFallback = defaultFallback;
        this.fallbacks = fallbacks;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }

    public String getDefaultFallback() {
        return this.defaultFallback;
    }

    public Collection<Fallback> getFallbacks() {
        return this.fallbacks;
    }
}
