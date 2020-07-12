package com.github.derrop.cloudnettransformer.cloud.cloudnet3.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;

import java.nio.file.Path;
import java.util.Collection;

public class CloudNet3MySQLDatabaseProvider implements DatabaseProvider {

    // TODO

    private final Path directory;

    public CloudNet3MySQLDatabaseProvider(Path directory) {
        this.directory = directory;
    }

    @Override
    public Database getDatabase(String name) {
        return null;
    }

    @Override
    public boolean containsDatabase(String name) {
        return false;
    }

    @Override
    public boolean deleteDatabase(String name) {
        return false;
    }

    @Override
    public Collection<String> getDatabaseNames() {
        return null;
    }
}
