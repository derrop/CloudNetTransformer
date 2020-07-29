package com.github.derrop.cloudnettransformer.cloud.cloudnet3.player;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;

import java.io.IOException;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "Players")
public class CloudNet3Players implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloudnet_cloud_players";

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (cloudSystem.getPlayerProvider() == null) {
            return false;
        }

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        PlayerProvider playerProvider = new CloudNet3PlayerProvider(database);

        cloudSystem.getPlayerProvider().loadPlayers(playerProvider::insertPlayer);

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        cloudSystem.setPlayerProvider(new CloudNet3PlayerProvider(database));

        return true;
    }
}
