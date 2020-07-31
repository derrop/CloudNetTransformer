package com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user;

import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.Permissible;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.Permission;

import java.util.Collection;
import java.util.UUID;

public class PermissionUser extends Permissible {

    private final UUID uniqueId;
    private final String prefix;
    private final String suffix;
    private final String hashedPassword;
    private final Collection<UserGroup> groups;

    public PermissionUser(String name, Collection<Permission> permissions, UUID uniqueId, String prefix, String suffix, String hashedPassword, Collection<UserGroup> groups) {
        super(name, permissions);
        this.uniqueId = uniqueId;
        this.prefix = prefix;
        this.suffix = suffix;
        this.hashedPassword = hashedPassword;
        this.groups = groups;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    public Collection<UserGroup> getGroups() {
        return this.groups;
    }

}
