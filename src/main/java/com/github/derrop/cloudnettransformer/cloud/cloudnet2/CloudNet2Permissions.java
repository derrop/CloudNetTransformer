package com.github.derrop.cloudnettransformer.cloud.cloudnet2;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.Permission;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.PermissionConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.PermissionGroup;
import com.github.derrop.cloudnettransformer.cloud.reader.CloudReader;
import com.github.derrop.cloudnettransformer.cloud.writer.CloudWriter;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class CloudNet2Permissions implements CloudWriter, CloudReader {
    @Override
    public String getName() {
        return "Permissions";
    }

    private Path config(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("local").resolve("perms.yml");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) {

        PermissionConfiguration permissionConfiguration = cloudSystem.getPermissionConfiguration();
        if (permissionConfiguration == null) {
            return false;
        }


        Map<String, Document> groups = new HashMap<>(permissionConfiguration.getGroups().size());

        for (PermissionGroup group : permissionConfiguration.getGroups()) {
            Document document = Documents.newDocument()
                    .append("prefix", group.getPrefix())
                    .append("suffix", group.getSuffix())
                    .append("display", group.getDisplay())
                    .append("color", group.getColor())
                    .append("tagId", group.getSortId())
                    .append("joinPower", 0)
                    .append("defaultGroup", group.isDefaultGroup())
                    .append("options", Documents.newDocument())
                    .append("implements", group.getGroups());

            Collection<String> permissions = new ArrayList<>();
            Map<String, Collection<String>> groupPermissions = new HashMap<>();

            for (Permission permission : group.getPermissions()) {
                String permissionName = (permission.getPotency() < 0 ? "-" : "") + permission.getName();

                if (permission.getTargetGroup() == null) {
                    permissions.add(permissionName);
                    continue;
                }

                if (!groupPermissions.containsKey(permission.getTargetGroup())) {
                    groupPermissions.put(permission.getTargetGroup(), new ArrayList<>());
                }
                groupPermissions.get(permission.getTargetGroup()).add(permissionName);
            }

            document.append("permissions", permissions).append("serverGroupPermissions", groupPermissions);

            groups.put(group.getName(), document);
        }

        Document document = Documents.newDocument()
                .append("enabled", permissionConfiguration.isEnabled())
                .append("groups", groups);

        Documents.yamlStorage().write(document, this.config(directory));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {
        Path configPath = this.config(directory);
        if (!Files.exists(configPath)) {
            return false;
        }

        Document document = Documents.yamlStorage().read(configPath);

        boolean enabled = document.getBoolean("enabled");

        Document groups = document.getDocument("groups");
        Collection<PermissionGroup> outGroups = new ArrayList<>(groups.size());

        for (String name : groups.keys()) {
            Document group = groups.getDocument(name);

            Collection<Permission> permissions = new ArrayList<>();
            this.parsePermissions(permissions, (permission, potency) -> new Permission(permission, potency, -1, null), group.getJsonArray("permissions"));

            Document serverGroupPermissions = group.getDocument("serverGroupPermissions");
            for (String targetGroup : serverGroupPermissions.keys()) {
                this.parsePermissions(permissions, (permission, potency) -> new Permission(permission, potency, -1, targetGroup), serverGroupPermissions.getJsonArray(targetGroup));
            }

            Collection<String> inheritedGroups = group.get("implements", TypeToken.getParameterized(Collection.class, String.class).getType());

            outGroups.add(new PermissionGroup(
                    name,
                    permissions,
                    inheritedGroups,
                    group.getString("prefix"),
                    group.getString("suffix"),
                    group.getString("display"),
                    group.getString("color"),
                    group.getInt("tagId"),
                    group.getBoolean("defaultGroup")
            ));
        }

        cloudSystem.setPermissionConfiguration(new PermissionConfiguration(enabled, outGroups));

        return true;
    }

    private void parsePermissions(Collection<Permission> permissions, BiFunction<String, Integer, Permission> permissionCreator, JsonArray array) {
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive() && !element.getAsJsonPrimitive().isString()) {
                continue;
            }

            String permission = element.getAsString();
            if (permission.isEmpty()) {
                continue;
            }

            boolean negative = permission.startsWith("-");

            permissions.add(permissionCreator.apply(negative ? permission.substring(1) : permission, negative ? -1 : 1));
        }
    }

}
