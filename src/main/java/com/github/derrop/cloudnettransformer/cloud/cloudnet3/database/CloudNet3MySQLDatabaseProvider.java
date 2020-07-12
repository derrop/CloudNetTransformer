package com.github.derrop.cloudnettransformer.cloud.cloudnet3.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import com.github.derrop.cloudnettransformer.util.ThrowableFunction;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class CloudNet3MySQLDatabaseProvider extends CloudNet3SQLDatabaseProvider {

    // TODO

    private final Path directory;

    public CloudNet3MySQLDatabaseProvider(Path directory) {
        this.directory = directory;
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String query, Object... objects) {
        return 0;
    }

    @Override
    public <T> T executeQuery(String query, ThrowableFunction<ResultSet, T, SQLException> function, Object... objects) {
        return null;
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
