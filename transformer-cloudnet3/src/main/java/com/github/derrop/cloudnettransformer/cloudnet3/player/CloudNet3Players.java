package com.github.derrop.cloudnettransformer.cloudnet3.player;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;

import java.nio.file.Path;

@DescribedCloudExecutor(name = "Players", priority = ExecutorPriority.LAST)
public class CloudNet3Players implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloudnet_cloud_players";

    @Override
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) {
        if (cloudSystem.getPlayerProvider() == null) {
            return ExecuteResult.failed("PlayerProvider in the CloudSystem not set");
        }

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        PlayerProvider playerProvider = new CloudNet3PlayerProvider(cloudSystem, database);

        cloudSystem.getPlayerProvider().loadPlayers(playerProvider::insertPlayer);
        cloudSystem.setPlayerProvider(playerProvider);

        return ExecuteResult.success();
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) {

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        cloudSystem.setPlayerProvider(new CloudNet3PlayerProvider(cloudSystem, database));

        return ExecuteResult.success();
    }
}
