package com.github.derrop.cloudnettransformer.cloud.cloudnet3.permission;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.Permission;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUser;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUserProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.UserGroup;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CloudNet3PermissionUserProvider implements PermissionUserProvider {

    private final CloudNet3Permissions permissions;
    private final Database database;

    public CloudNet3PermissionUserProvider(CloudNet3Permissions permissions, Database database) {
        this.permissions = permissions;
        this.database = database;
    }

    @Override
    public long getRegisteredCount() {
        return this.database.getDocumentsCount();
    }

    private PermissionUser asUser(Document document) {
        if (document == null) {
            return null;
        }

        return new PermissionUser(
                document.getString("name"),
                this.permissions.getAllPermissions(document),
                document.get("uniqueId", UUID.class),
                null, null,
                document.getString("hashedPassword"),
                document.getDocuments("groups", Collections.emptyList()).stream().map(group -> new UserGroup(group.getString("group"), group.getLong("timeOutMillis"))).collect(Collectors.toList())
        );
    }

    @Override
    public void loadUsers(Consumer<PermissionUser> consumer) {
        this.database.iterate((key, document) -> {
            PermissionUser user = this.asUser(document);
            if (user != null) {
                consumer.accept(user);
            }
        });
    }

    @Override
    public void insertUser(PermissionUser permissionUser) {
        String uuidString = permissionUser.getUniqueId().toString();

        Collection<Document> permissions = permissionUser.getPermissions().stream()
                .filter(permission -> permission.getTargetGroup() == null)
                .map(this.permissions::asJson)
                .collect(Collectors.toList());
        Map<String, Collection<Document>> groupPermissions = new HashMap<>();
        for (Permission permission : permissionUser.getPermissions()) {
            if (permission.getTargetGroup() == null) {
                continue;
            }
            groupPermissions.computeIfAbsent(permission.getTargetGroup(), s -> new ArrayList<>()).add(this.permissions.asJson(permission));
        }

        Document document = Documents.newDocument()
                .append("uniqueId", uuidString)
                .append("groups", permissionUser.getGroups().stream().map(group -> Documents.newDocument().append("group", group.getName()).append("timeOutMillis", group.getTimeout())).collect(Collectors.toList()))
                .append("hashedPassword", permissionUser.getHashedPassword())
                .append("createdTime", System.currentTimeMillis())
                .append("name", permissionUser.getName())
                .append("potency", 0)
                .append("permissions", permissions)
                .append("groupPermissions", groupPermissions)
                .append("properties", Documents.newDocument());

        this.database.insert(uuidString, document);
    }

    @Override
    public boolean containsUser(UUID uniqueId) {
        return this.database.contains(uniqueId.toString());
    }

    @Override
    public PermissionUser getUser(UUID uniqueId) {
        return this.asUser(this.database.get(uniqueId.toString()));
    }

}
