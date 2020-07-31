package com.github.derrop.cloudnettransformer.cloudnet3.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@DescribedCloudExecutor(name = "Database", priority = ExecutorPriority.FIRST)
public class CloudNet3Database implements CloudExecutor {

    private static final Map<String, Class<? extends DatabaseProvider>> AVAILABLE_PROVIDERS = new HashMap<>();

    static {
        AVAILABLE_PROVIDERS.put("h2", CloudNet3H2DatabaseProvider.class);
        AVAILABLE_PROVIDERS.put("mysql", CloudNet3MySQLDatabaseProvider.class);
    }

    @Override
    public boolean execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {
        Path registryFile = directory.resolve("local").resolve("registry");

        String providerName = "h2";

        if (Files.exists(registryFile)) {
            Document registry = Documents.jsonStorage().read(registryFile);
            if (!registry.contains("entries")) {
                return false;
            }
            Document registryEntries = registry.getDocument("entries");
            if (registryEntries == null || !registryEntries.contains("database_provider")) {
                return false;
            }

            providerName = registryEntries.getString("database_provider");
        }

        if (providerName == null) {
            return false;
        }

        Class<? extends DatabaseProvider> providerClass = AVAILABLE_PROVIDERS.get(providerName);
        if (providerClass == null) {
            System.err.println("Unknown DatabaseProvider '" + providerName + "'");
            return false;
        }

        try {
            DatabaseProvider databaseProvider = providerClass.getDeclaredConstructor(Path.class).newInstance(directory);
            if (!databaseProvider.init()) {
                return false;
            }

            cloudSystem.setDatabaseProvider(databaseProvider);

            return true;
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
