package com.github.derrop.cloudnettransformer.cloud.deserialized;

import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.PermissionConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.SignConfiguration;

public class CloudSystem {

    private SignConfiguration signConfiguration;
    private PermissionConfiguration permissionConfiguration;

    public SignConfiguration getSignConfiguration() {
        return this.signConfiguration;
    }

    public void setSignConfiguration(SignConfiguration signConfiguration) {
        this.signConfiguration = signConfiguration;
    }

    public PermissionConfiguration getPermissionConfiguration() {
        return this.permissionConfiguration;
    }

    public void setPermissionConfiguration(PermissionConfiguration permissionConfiguration) {
        this.permissionConfiguration = permissionConfiguration;
    }
}
