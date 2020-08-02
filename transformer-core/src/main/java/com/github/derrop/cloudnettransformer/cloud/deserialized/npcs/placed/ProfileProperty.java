package com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed;

public class ProfileProperty {

    private final String name;
    private final String value;
    private final String signature;

    public ProfileProperty(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getSignature() {
        return this.signature;
    }
}
