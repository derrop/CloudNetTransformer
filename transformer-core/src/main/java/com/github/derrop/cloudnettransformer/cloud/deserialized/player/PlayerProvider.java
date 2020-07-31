package com.github.derrop.cloudnettransformer.cloud.deserialized.player;

import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerProvider {

    long getRegisteredCount();

    void loadPlayers(Consumer<RegisteredPlayer> consumer);

    void insertPlayer(RegisteredPlayer player);

    boolean containsPlayer(UUID uniqueId);

    RegisteredPlayer getPlayer(UUID uniqueId);

}
