package com.github.derrop.cloudnettransformer.cloud.deserialized.permissions;

import java.util.Collection;

public class Permissible {

    private final String name;
    private final Collection<Permission> permissions;

    public Permissible(String name, Collection<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return this.name;
    }

    public Collection<Permission> getPermissions() {
        return this.permissions;
    }

}
