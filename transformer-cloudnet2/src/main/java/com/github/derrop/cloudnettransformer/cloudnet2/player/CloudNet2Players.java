package com.github.derrop.cloudnettransformer.cloudnet2.player;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUserProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;

import java.nio.file.Path;

@DescribedCloudExecutor(name = "Players/Permissions")
public class CloudNet2Players implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloudnet_internal_players";

    private Path usersFile(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("users.json");
    }

    @Override
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);

        ExecuteResult result = ExecuteResult.failed("No PlayerProvider/PermissionUserProvider set in the CloudSystem");

        if (cloudSystem.getPlayerProvider() != null) {
            PlayerProvider playerProvider = new CloudNet2PlayerProvider(database);
            cloudSystem.getPlayerProvider().loadPlayers(playerProvider::insertPlayer);
            cloudSystem.setPlayerProvider(playerProvider);
            result = ExecuteResult.success();
        }
        if (cloudSystem.getPermissionUserProvider() != null) {
            PermissionUserProvider permissionUserProvider = new CloudNet2PermissionUserProvider(cloudSystem.getPlayerProvider(), database, this.usersFile(directory));
            cloudSystem.getPermissionUserProvider().loadUsers(permissionUserProvider::insertUser);
            cloudSystem.setPermissionUserProvider(permissionUserProvider);
            result = ExecuteResult.success();
        }

        return result;
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);

        cloudSystem.setPlayerProvider(new CloudNet2PlayerProvider(database));
        cloudSystem.setPermissionUserProvider(new CloudNet2PermissionUserProvider(cloudSystem.getPlayerProvider(), database, this.usersFile(directory)));

        return ExecuteResult.success();
    }

}
