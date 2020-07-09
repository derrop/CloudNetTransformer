package com.github.derrop.cloudnettransformer.cloud.deserialized.permissions;

import java.util.Collection;

public class PermissionGroup extends Permissible {
    private final String prefix;
    private final String suffix;
    private final String display;
    private final String color;
    private final int sortId;
    private final boolean defaultGroup;

    public PermissionGroup(String name, Collection<Permission> permissions, Collection<String> groups, String prefix, String suffix, String display, String color, int sortId, boolean defaultGroup) {
        super(name, permissions, groups);
        this.prefix = prefix;
        this.suffix = suffix;
        this.display = display;
        this.color = color;
        this.sortId = sortId;
        this.defaultGroup = defaultGroup;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getDisplay() {
        return this.display;
    }

    public String getColor() {
        return this.color;
    }

    public int getSortId() {
        return this.sortId;
    }

    public boolean isDefaultGroup() {
        return this.defaultGroup;
    }
}
