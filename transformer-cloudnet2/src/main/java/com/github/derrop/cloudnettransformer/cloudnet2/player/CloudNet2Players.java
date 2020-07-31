package com.github.derrop.cloudnettransformer.cloudnet2.player;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUserProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;

import java.io.IOException;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "Players/Permissions")
public class CloudNet2Players implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloudnet_internal_players";

    private Path usersFile(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("users.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);

        boolean success = false;

        if (cloudSystem.getPlayerProvider() != null) {
            PlayerProvider playerProvider = new CloudNet2PlayerProvider(database);
            cloudSystem.getPlayerProvider().loadPlayers(playerProvider::insertPlayer);
            cloudSystem.setPlayerProvider(playerProvider);
            success = true;
        }
        if (cloudSystem.getPermissionUserProvider() != null) {
            PermissionUserProvider permissionUserProvider = new CloudNet2PermissionUserProvider(cloudSystem.getPlayerProvider(), database, this.usersFile(directory));
            cloudSystem.getPermissionUserProvider().loadUsers(permissionUserProvider::insertUser);
            cloudSystem.setPermissionUserProvider(permissionUserProvider);
            success = true;
        }

        return success;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);

        cloudSystem.setPlayerProvider(new CloudNet2PlayerProvider(database));
        cloudSystem.setPermissionUserProvider(new CloudNet2PermissionUserProvider(cloudSystem.getPlayerProvider(), database, this.usersFile(directory)));

        return true;
    }

}
