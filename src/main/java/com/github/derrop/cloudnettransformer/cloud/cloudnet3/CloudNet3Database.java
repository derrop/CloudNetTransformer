package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.cloudnet3.database.CloudNet3H2DatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.cloudnet3.database.CloudNet3MySQLDatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.reader.CloudReader;
import com.github.derrop.cloudnettransformer.document.Document;
import com.github.derrop.cloudnettransformer.document.Documents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CloudNet3Database implements CloudReader {

    private static final Map<String, Class<? extends DatabaseProvider>> AVAILABLE_PROVIDERS = new HashMap<>();

    static {
        AVAILABLE_PROVIDERS.put("h2", CloudNet3H2DatabaseProvider.class);
        AVAILABLE_PROVIDERS.put("mysql", CloudNet3MySQLDatabaseProvider.class);
    }

    @Override
    public String getName() {
        return "Database";
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        Path registryFile = directory.resolve("local").resolve("registry");
        if (!Files.exists(registryFile)) {
            return false;
        }

        Document registry = Documents.jsonStorage().read(registryFile);
        if (!registry.contains("entries")) {
            return false;
        }
        Document registryEntries = registry.getDocument("entries");
        if (registryEntries == null || !registryEntries.contains("database_provider")) {
            return false;
        }

        String providerName = registryEntries.getString("database_provider");
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
