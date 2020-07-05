package com.github.derrop.cloudnettransformer.cloud.deserialized;

import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.SignConfiguration;

public class CloudSystem {

    private SignConfiguration signConfiguration;

    public SignConfiguration getSignConfiguration() {
        return this.signConfiguration;
    }

    public void setSignConfiguration(SignConfiguration signConfiguration) {
        this.signConfiguration = signConfiguration;
    }
}
