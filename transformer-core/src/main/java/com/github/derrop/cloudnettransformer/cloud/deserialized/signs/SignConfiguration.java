package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

import java.util.Collection;

public class SignConfiguration {

    private final Collection<GroupSignConfiguration> configurations;

    public SignConfiguration(Collection<GroupSignConfiguration> configurations) {
        this.configurations = configurations;
    }

    public Collection<GroupSignConfiguration> getConfigurations() {
        return this.configurations;
    }

}
