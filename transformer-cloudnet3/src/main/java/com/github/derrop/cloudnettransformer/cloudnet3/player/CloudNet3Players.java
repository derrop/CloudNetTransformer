package com.github.derrop.cloudnettransformer.cloudnet3.player;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;

import java.io.IOException;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "Players", priority = ExecutorPriority.LAST)
public class CloudNet3Players implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloudnet_cloud_players";

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (cloudSystem.getPlayerProvider() == null) {
            return false;
        }

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        PlayerProvider playerProvider = new CloudNet3PlayerProvider(cloudSystem, database);

        cloudSystem.getPlayerProvider().loadPlayers(playerProvider::insertPlayer);
        cloudSystem.setPlayerProvider(playerProvider);

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        cloudSystem.setPlayerProvider(new CloudNet3PlayerProvider(cloudSystem, database));

        return true;
    }
}
