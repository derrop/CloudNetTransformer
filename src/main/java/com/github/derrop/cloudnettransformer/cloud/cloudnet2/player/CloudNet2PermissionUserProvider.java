package com.github.derrop.cloudnettransformer.cloud.cloudnet2.player;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.Permission;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUser;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUserProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.UserGroup;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.util.StringUtils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CloudNet2PermissionUserProvider implements PermissionUserProvider {

    private final PlayerProvider playerProvider;
    private final Database database;
    private final Path usersFile;

    public CloudNet2PermissionUserProvider(PlayerProvider playerProvider, Database database, Path usersFile) {
        this.playerProvider = playerProvider;
        this.database = database;
        this.usersFile = usersFile;
    }

    @Override
    public long getRegisteredCount() {
        return this.database.getDocumentsCount();
    }

    private PermissionUser asUser(Document document) {
        if (document == null) {
            return null;
        }
        Document offlinePlayer = document.getDocument("offlinePlayer");
        if (offlinePlayer == null) {
            return null;
        }

        Document permissionEntity = offlinePlayer.getDocument("permissionEntity");
        if (permissionEntity == null) {
            return null;
        }

        Collection<Permission> permissions = new ArrayList<>();
        Document permissionsDocument = permissionEntity.getDocument("permissions");
        if (permissionsDocument != null) {
            for (String permission : permissionsDocument.keys()) {
                permissions.add(new Permission(permission, permissionsDocument.getBoolean(permission) ? 1 : -1, -1, null));
            }
        }

        return new PermissionUser(
                offlinePlayer.getString("name"),
                permissions,
                offlinePlayer.get("uniqueId", UUID.class),
                permissionEntity.getString("prefix"),
                permissionEntity.getString("suffix"),
                null,
                permissionEntity.getDocuments("groups").stream().map(group -> new UserGroup(group.getString("group"), group.getLong("timeout") <= 0 ? -1 : group.getLong("timeout"))).collect(Collectors.toList())
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
    public void insertUser(PermissionUser user) {
        String uuidString = user.getUniqueId().toString();

        if (this.playerProvider == null || !this.playerProvider.containsPlayer(user.getUniqueId())) {
            Document document = Files.exists(this.usersFile) ? Documents.jsonStorage().read(this.usersFile) : null;

            if (document == null || !document.contains("users")) {
                document = Documents.newDocument("users", new ArrayList<>());
            }

            Collection<Document> users = document.getDocuments("users");
            Document userDocument = Documents.newDocument()
                    .append("name", user.getName())
                    .append("uniqueId", uuidString)
                    .append("hashedPassword", user.getHashedPassword())
                    .append("apiToken", StringUtils.randomString(32))
                    .append("permissions", user.getPermissions().stream().filter(permission -> permission.getPotency() >= 0).map(Permission::getName).collect(Collectors.toList()))
                    .append("metaData", Documents.newDocument());

            users.add(userDocument);
            document.append("users", users);

            Documents.jsonStorage().write(document, this.usersFile);
            return;
        }

        Map<String, Boolean> permissions = user.getPermissions().stream().collect(Collectors.toMap(Permission::getName, permission -> permission.getPotency() > 0));
        Document permissionEntity = Documents.newDocument()
                .append("uniqueId", uuidString)
                .append("permissions", permissions)
                .append("prefix", user.getPrefix())
                .append("suffix", user.getSuffix())
                .append("groups", user.getGroups().stream().map(group -> Documents.newDocument().append("group", group.getName()).append("timeout", group.getTimeout())).collect(Collectors.toList()));

        if (this.database.contains(uuidString)) {
            Document fullDocument = this.database.get(uuidString);
            Document offlinePlayer = fullDocument.getDocument("offlinePlayer");
            offlinePlayer.append("permissionEntity", permissionEntity);
            fullDocument.append("offlinePlayer", offlinePlayer);
            this.database.update(uuidString, fullDocument);
        } else {
            Document lastPlayerConnection = Documents.newDocument()
                    .append("uniqueId", uuidString)
                    .append("name", user.getName())
                    .append("host", "127.0.0.1")
                    .append("port", 65535)
                    .append("onlineMode", true)
                    .append("legacy", false);

            Document offlinePlayer = Documents.newDocument()
                    .append("uniqueId", uuidString)
                    .append("name", user.getName())
                    .append("metaData", Documents.newDocument().append("name", (String) null).append("dataCatcher", Documents.newDocument()).append("file", (String) null))
                    .append("firstLogin", -1L)
                    .append("lastLogin", -1L)
                    .append("lastPlayerConnection", lastPlayerConnection)
                    .append("permissionEntity", permissionEntity);

            this.database.insert(uuidString, Documents.newDocument().append("offlinePlayer", offlinePlayer));
        }
    }

    @Override
    public boolean containsUser(UUID uniqueId) {
        if (this.database.contains(uniqueId.toString())) {
            return true;
        }
        if (Files.notExists(this.usersFile)) {
            return false;
        }
        Document document = Documents.jsonStorage().read(this.usersFile);
        Collection<Document> users = document.getDocuments("users");
        return users.stream().anyMatch(user -> user.get("uniqueId", UUID.class).equals(uniqueId));
    }

    @Override
    public PermissionUser getUser(UUID uniqueId) {
        return this.asUser(this.database.get(uniqueId.toString()));
    }
}
