package com.github.derrop.cloudnettransformer.cloudnet3.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import com.github.derrop.cloudnettransformer.util.ThrowableFunction;
import com.google.common.base.Preconditions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class CloudNet3SQLDatabaseProvider implements DatabaseProvider {

    @Override
    public boolean containsDatabase(String name) {
        Preconditions.checkNotNull(name);

        for (String database : this.getDatabaseNames()) {
            if (database.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Database getDatabase(String name) {
        return new CloudNet3SQLDatabase(this, name);
    }

    public abstract Connection getConnection() throws SQLException;

    public abstract int executeUpdate(String query, Object... objects);

    public abstract <T> T executeQuery(String query, ThrowableFunction<ResultSet, T, SQLException> function, Object... objects);

}
