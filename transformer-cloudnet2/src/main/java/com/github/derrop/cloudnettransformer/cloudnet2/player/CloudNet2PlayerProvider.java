package com.github.derrop.cloudnettransformer.cloudnet2.player;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.RegisteredPlayer;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

public class CloudNet2PlayerProvider implements PlayerProvider {

    private final Database database;

    public CloudNet2PlayerProvider(Database database) {
        this.database = database;
    }

    @Override
    public long getRegisteredCount() {
        return this.database.getDocumentsCount();
    }

    private RegisteredPlayer asPlayer(Document document) {
        if (document == null) {
            return null;
        }
        Document offlinePlayer = document.getDocument("offlinePlayer");
        if (offlinePlayer == null) {
            return null;
        }

        Document lastPlayerConnection = offlinePlayer.getDocument("lastPlayerConnection");
        if (lastPlayerConnection == null) {
            return null;
        }

        return new RegisteredPlayer(
                offlinePlayer.get("uniqueId", UUID.class),
                offlinePlayer.getString("name"),
                null,
                offlinePlayer.getLong("firstLogin"),
                offlinePlayer.getLong("lastLogin"),
                lastPlayerConnection.getInt("version"),
                lastPlayerConnection.getString("host"),
                lastPlayerConnection.getInt("port")
        );
    }

    @Override
    public void loadPlayers(Consumer<RegisteredPlayer> consumer) {
        this.database.iterate((key, document) -> {
            RegisteredPlayer player = this.asPlayer(document);
            if (player != null) {
                consumer.accept(player);
            }
        });
    }

    @Override
    public void insertPlayer(RegisteredPlayer player) {
        String uuidString = player.getUniqueId().toString();
        boolean contains = this.database.contains(uuidString);

        Document permissionEntity;
        if (contains) {
            Document document = this.database.get(uuidString);
            Document offlinePlayer = document.getDocument("offlinePlayer");
            if (offlinePlayer == null) {
                return;
            }
            permissionEntity = offlinePlayer.getDocument("permissionEntity");
            if (permissionEntity == null) {
                return;
            }
        } else {
            permissionEntity = Documents.newDocument()
                    .append("uniqueId", uuidString)
                    .append("prefix", (String) null)
                    .append("suffix", (String) null)
                    .append("permissions", Documents.newDocument())
                    .append("groups", Collections.emptyList());
        }

        Document lastPlayerConnection = Documents.newDocument()
                .append("uniqueId", uuidString)
                .append("name", player.getName())
                .append("version", player.getLastVersion())
                .append("host", player.getLastHost())
                .append("port", player.getLastPort())
                .append("onlineMode", true)
                .append("legacy", false);

        Document offlinePlayer = Documents.newDocument()
                .append("uniqueId", uuidString)
                .append("name", player.getName())
                .append("metaData", Documents.newDocument().append("name", (String) null).append("dataCatcher", Documents.newDocument()).append("file", (String) null))
                .append("firstLogin", player.getFirstLogin())
                .append("lastLogin", player.getLastLogin())
                .append("lastPlayerConnection", lastPlayerConnection)
                .append("permissionEntity", permissionEntity);

        Document document = Documents.newDocument().append("offlinePlayer", offlinePlayer);

        if (contains) {
            this.database.update(uuidString, document);
        } else {
            this.database.insert(uuidString, document);
        }
    }

    @Override
    public boolean containsPlayer(UUID uniqueId) {
        return this.database.contains(uniqueId.toString());
    }

    @Override
    public RegisteredPlayer getPlayer(UUID uniqueId) {
        return this.asPlayer(this.database.get(uniqueId.toString()));
    }
}
