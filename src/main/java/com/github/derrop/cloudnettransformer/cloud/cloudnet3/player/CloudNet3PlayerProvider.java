package com.github.derrop.cloudnettransformer.cloud.cloudnet3.player;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.RegisteredPlayer;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceEnvironment;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

public class CloudNet3PlayerProvider implements PlayerProvider {

    private final Database database;

    public CloudNet3PlayerProvider(Database database) {
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
        Document lastNetworkConnectionInfo = document.getDocument("lastNetworkConnectionInfo");
        if (lastNetworkConnectionInfo == null) {
            return null;
        }
        Document address = lastNetworkConnectionInfo.getDocument("address");

        return new RegisteredPlayer(
                document.get("uniqueId", UUID.class),
                document.getString("name"),
                document.getString("xBoxId"),
                document.getLong("firstLoginTimeMillis"),
                document.getLong("lastLoginTimeMillis"),
                lastNetworkConnectionInfo.getInt("version"),
                address != null ? address.getString("host") : "127.0.0.1",
                address != null ? address.getInt("port") : 65535
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

        Document serviceId = Documents.newDocument().append("uniqueId", UUID.randomUUID()).append("nodeUniqueId", "Node-1").append("taskName", "Proxy").append("taskServiceId", 1).append("environment", ServiceEnvironment.BUNGEECORD);
        Document lastNetworkConnectionInfo = Documents.newDocument()
                .append("uniqueId", uuidString)
                .append("name", player.getName())
                .append("version", player.getLastVersion())
                .append("address", Documents.newDocument().append("host", player.getLastHost()).append("port", player.getLastPort()))
                .append("listener", Documents.newDocument().append("host", "0.0.0.0").append("port", 25565))
                .append("onlineMode", true)
                .append("legacy", false)
                .append("networkService", Documents.newDocument().append("serviceId", serviceId).append("groups", Collections.singletonList("Proxy")));

        Document document = Documents.newDocument()
                .append("uniqueId", uuidString)
                .append("name", player.getName())
                .append("xBoxId", player.getXboxId())
                .append("firstLoginTimeMillis", player.getFirstLogin())
                .append("lastLoginTimeMillis", player.getLastLogin())
                .append("lastNetworkConnectionInfo", lastNetworkConnectionInfo)
                .append("properties", Documents.newDocument());

        this.database.insert(uuidString, document);
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
