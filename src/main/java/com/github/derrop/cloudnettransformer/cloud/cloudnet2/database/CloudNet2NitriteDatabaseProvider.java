package com.github.derrop.cloudnettransformer.cloud.cloudnet2.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;

import java.nio.file.Path;
import java.util.Collection;

public class CloudNet2NitriteDatabaseProvider implements DatabaseProvider {

    // TODO

    private final Path path;

    public CloudNet2NitriteDatabaseProvider(Path path) {
        this.path = path;
    }

    @Override
    public boolean init() {
        throw new UnsupportedOperationException("Nitrite is not implemented yet");
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

    @Override
    public void close() {
    }
}
