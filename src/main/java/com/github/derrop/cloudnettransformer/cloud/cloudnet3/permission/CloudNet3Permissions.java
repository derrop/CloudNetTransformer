package com.github.derrop.cloudnettransformer.cloud.cloudnet3.permission;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.Permission;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.group.PermissionConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.group.PermissionGroup;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUserProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.FileDownloader;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@DescribedCloudExecutor(name = "PermissionGroups")
public class CloudNet3Permissions extends FileDownloader implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloudnet_permission_users";

    public CloudNet3Permissions() {
        super("https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet-v3/job/master/lastSuccessfulBuild/artifact/cloudnet-modules/cloudnet-cloudperms/build/libs/cloudnet-cloudperms.jar", "modules/cloudnet-cloudperms.jar");
    }

    private Path config(Path directory) {
        return directory.resolve("local").resolve("permissions.json");
    }

    private Path moduleConfig(Path directory) {
        return directory.resolve("modules").resolve("CloudNet-CloudPerms").resolve("config.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (cloudSystem.getPermissionConfiguration() == null) {
            return false;
        }
        if (!super.downloadFile(directory)) {
            return false;
        }

        PermissionConfiguration permissionConfiguration = cloudSystem.getPermissionConfiguration();

        Path moduleConfig = this.moduleConfig(directory);
        Documents.jsonStorage().write(Documents.newDocument().append("enabled", permissionConfiguration.isEnabled()).append("excludedGroups", new String[0]), moduleConfig);

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

        if (cloudSystem.getPermissionUserProvider() != null) {
            Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
            PermissionUserProvider permissionUserProvider = new CloudNet3PermissionUserProvider(this, database);

            cloudSystem.getPermissionUserProvider().loadUsers(permissionUserProvider::insertUser);
        }

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {
        Path configPath = this.config(directory);
        if (!Files.exists(configPath)) {
            return false;
        }


        Document document = Documents.jsonStorage().read(configPath);
        JsonArray array = document.getJsonArray("groups");

        Collection<PermissionGroup> groups = new ArrayList<>(array.size());

        for (JsonElement element : array) {
            Document group = Documents.newDocument(element.getAsJsonObject());

            groups.add(new PermissionGroup(
                    group.getString("name"),
                    this.getAllPermissions(group),
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
        boolean enabled = Files.exists(moduleConfig) && Documents.jsonStorage().read(moduleConfig).getBoolean("enabled");

        cloudSystem.setPermissionConfiguration(new PermissionConfiguration(enabled, groups));

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        cloudSystem.setPermissionUserProvider(new CloudNet3PermissionUserProvider(this, database));

        return true;
    }

    protected Collection<Permission> getAllPermissions(Document document) {
        Collection<Permission> permissions = new ArrayList<>();

        this.asPermissions(permissions, null, document.getDocuments("permissions"));

        Document groupPermissions = document.getDocument("groupPermissions");
        for (String targetGroup : groupPermissions.keys()) {
            this.asPermissions(permissions, targetGroup, groupPermissions.getDocuments(targetGroup));
        }

        return permissions;
    }

    private Permission asPermission(String targetGroup, Document document) {
        return new Permission(
                document.getString("name"),
                document.getInt("potency"),
                document.getLong("timeOutMillis"),
                targetGroup
        );
    }

    private void asPermissions(Collection<Permission> permissions, String targetGroup, Collection<Document> inputPermissions) {
        for (Document document : inputPermissions) {
            permissions.add(this.asPermission(targetGroup, document));
        }
    }

    protected Document asJson(Permission permission) {
        return Documents.newDocument()
                .append("name", permission.getName())
                .append("potency", permission.getPotency())
                .append("timeOutMillis", permission.getTimeout());
    }

}
