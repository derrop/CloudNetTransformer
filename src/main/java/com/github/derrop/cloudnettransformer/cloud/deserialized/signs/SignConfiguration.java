package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;

import java.util.Collection;
import java.util.Map;

public class SignConfiguration {

    private final Collection<GroupSignConfiguration> configurations;
    private final Map<MessageType, String> messages;

    public SignConfiguration(Collection<GroupSignConfiguration> configurations, Map<MessageType, String> messages) {
        this.configurations = configurations;
        this.messages = messages;
    }

    public Collection<GroupSignConfiguration> getConfigurations() {
        return this.configurations;
    }

    public Map<MessageType, String> getMessages() {
        return this.messages;
    }
}
