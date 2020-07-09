package com.github.derrop.cloudnettransformer.cloud.deserialized.permissions;

import java.util.Collection;

public class Permissible {

    private final String name;
    private final Collection<Permission> permissions;
    private final Collection<String> groups;

    public Permissible(String name, Collection<Permission> permissions, Collection<String> groups) {
        this.name = name;
        this.permissions = permissions;
        this.groups = groups;
    }

    public String getName() {
        return this.name;
    }

    public Collection<Permission> getPermissions() {
        return this.permissions;
    }

    public Collection<String> getGroups() {
        return this.groups;
    }
}
