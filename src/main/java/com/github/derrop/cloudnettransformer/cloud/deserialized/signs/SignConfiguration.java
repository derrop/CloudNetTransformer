package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

import java.util.Collection;
import java.util.Map;

public class SignConfiguration {

    private final Collection<GroupSignConfiguration> configurations;
    private final Map<SignMessage, String> messages;

    public SignConfiguration(Collection<GroupSignConfiguration> configurations, Map<SignMessage, String> messages) {
        this.configurations = configurations;
        this.messages = messages;
    }

    public Collection<GroupSignConfiguration> getConfigurations() {
        return this.configurations;
    }

    public Map<SignMessage, String> getMessages() {
        return this.messages;
    }
}
