package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.Permission;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.PermissionConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.PermissionGroup;
import com.github.derrop.cloudnettransformer.cloud.reader.CloudReader;
import com.github.derrop.cloudnettransformer.cloud.writer.CloudWriter;
import com.github.derrop.cloudnettransformer.document.Document;
import com.github.derrop.cloudnettransformer.document.Documents;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CloudNet3Permissions implements CloudReader, CloudWriter {

    private static final Type PERMISSION_COLLECTION_TYPE = TypeToken.getParameterized(Collection.class, Permission.class).getType();

    @Override
    public String getName() {
        return "Permissions";
    }

    private Path config(Path directory) {
        return directory.resolve("local").resolve("permissions.json");
    }

    private Path moduleConfig(Path directory) {
        return directory.resolve("modules").resolve("CloudNet-CloudPerms").resolve("config.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) {
        if (cloudSystem.getPermissionConfiguration() == null) {
            return false;
        }

        PermissionConfiguration permissionConfiguration = cloudSystem.getPermissionConfiguration();

        Path moduleConfig = this.moduleConfig(directory);
        Documents.jsonStorage().write(Documents.newDocument().append("enabled", permissionConfiguration.isEnabled()).append("groups", new String[0]), moduleConfig);

        Collection<Document> groups = new ArrayList<>(permissionConfiguration.getGroups().size());

        for (PermissionGroup group : permissionConfiguration.getGroups()) {

            Collection<Document> permissions = new ArrayList<>();
            Map<String, Collection<Document>> groupPermissions = new HashMap<>();

            for (Permission permission : group.getPermissions()) {
                if (permission.getTargetGroup() == null) {
                    permissions.add(this.asJson(permission));
                    continue;
                }

                if (!groupPermissions.containsKey(permission.getTargetGroup())) {
                    groupPermissions.put(permission.getTargetGroup(), new ArrayList<>());
                }
                groupPermissions.get(permission.getTargetGroup()).add(this.asJson(permission));
            }


            groups.add(Documents.newDocument()
                    .append("name", group.getName())
                    .append("prefix", group.getPrefix())
                    .append("suffix", group.getSuffix())
                    .append("display", group.getDisplay())
                    .append("sortId", group.getSortId())
                    .append("defaultGroup", group.isDefaultGroup())
                    .append("createdTime", System.currentTimeMillis())
                    .append("potency", -group.getSortId())
                    .append("permissions", permissions)
                    .append("groupPermissions", groupPermissions)
                    .append("groups", group.getGroups())
                    .append("properties", Documents.newDocument()));
        }

        Documents.jsonStorage().write(Documents.newDocument().append("groups", groups), this.config(directory));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {
        Path configPath = this.config(directory);
        if (!Files.exists(configPath)) {
            return false;
        }


        Document document = Documents.newJsonDocument(configPath);
        JsonArray array = document.getJsonArray("groups");

        Collection<PermissionGroup> groups = new ArrayList<>(array.size());

        for (JsonElement element : array) {
            Document group = Documents.newDocument(element.getAsJsonObject());

            Collection<Permission> permissions = new ArrayList<>();
            this.asPermissions(permissions, null, group.getJsonArray("permissions"));

            Document groupPermissions = group.getDocument("groupPermissions");
            for (String targetGroup : groupPermissions.keys()) {
                this.asPermissions(permissions, targetGroup, groupPermissions.getJsonArray(targetGroup));
            }

            groups.add(new PermissionGroup(
                    group.getString("name"),
                    permissions,
                    group.get("groups", TypeToken.getParameterized(Collection.class, String.class).getType()),
                    group.getString("prefix"),
                    group.getString("suffix"),
                    group.getString("display"),
                    group.getString("color"),
                    group.getInt("sortId"),
                    group.getBoolean("defaultGroup")
            ));
        }

        Path moduleConfig = this.moduleConfig(directory);
        boolean enabled = Files.exists(moduleConfig) && Documents.newJsonDocument(moduleConfig).getBoolean("enabled");

        cloudSystem.setPermissionConfiguration(new PermissionConfiguration(enabled, groups));

        return true;
    }

    private Permission asPermission(String targetGroup, Document document) {
        return new Permission(
                document.getString("name"),
                document.getInt("potency"),
                document.getLong("timeOutMillis"),
                targetGroup
        );
    }

    private void asPermissions(Collection<Permission> permissions, String targetGroup, JsonArray array) {
        for (JsonElement element : array) {
            permissions.add(this.asPermission(targetGroup, Documents.newDocument(element)));
        }
    }

    private Document asJson(Permission permission) {
        return Documents.newDocument()
                .append("name", permission.getName())
                .append("potency", permission.getPotency())
                .append("timeOutMillis", permission.getTimeout());
    }

}
