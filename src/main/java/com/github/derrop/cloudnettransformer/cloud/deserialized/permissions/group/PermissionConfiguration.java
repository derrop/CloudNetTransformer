package com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.group;

import java.util.Collection;

public class PermissionConfiguration {

    private final boolean enabled;
    private final Collection<PermissionGroup> groups;

    public PermissionConfiguration(boolean enabled, Collection<PermissionGroup> groups) {
        this.enabled = enabled;
        this.groups = groups;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Collection<PermissionGroup> getGroups() {
        return this.groups;
    }
}
