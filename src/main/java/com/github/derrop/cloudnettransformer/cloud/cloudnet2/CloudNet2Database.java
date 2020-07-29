package com.github.derrop.cloudnettransformer.cloud.cloudnet2;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.cloudnet2.database.CloudNet2FileDatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.cloudnet2.database.CloudNet2NitriteDatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "Database", types = ExecutorType.READ, priority = ExecutorPriority.FIRST)
public class CloudNet2Database implements CloudExecutor {

    @Override
    public boolean execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {
        Path databaseDirectory = directory.resolve(Constants.MASTER_DIRECTORY).resolve("database");
        if (!Files.exists(databaseDirectory) || !Files.isDirectory(databaseDirectory)) {
            return false;
        }

        Path nitriteUpgradeFile = databaseDirectory.resolve(".upgraded_nitrite");
        boolean nitrite = Files.exists(nitriteUpgradeFile) && Files.isDirectory(nitriteUpgradeFile);

        DatabaseProvider databaseProvider = nitrite ? new CloudNet2NitriteDatabaseProvider(databaseDirectory.resolve("cloudnet.db")) : new CloudNet2FileDatabaseProvider(databaseDirectory);
        if (!databaseProvider.init()) {
            return false;
        }

        cloudSystem.setDatabaseProvider(databaseProvider);

        return true;
    }

}
