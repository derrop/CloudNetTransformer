package com.github.derrop.cloudnettransformer.cloud.cloudnet3.database;

import com.github.derrop.cloudnettransformer.util.ThrowableFunction;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class CloudNet3H2DatabaseProvider extends CloudNet3SQLDatabaseProvider {

    private final Path h2File;

    private Connection connection;

    public CloudNet3H2DatabaseProvider(Path directory) {
        this.h2File = directory.resolve("local").resolve("database").resolve("h2");
    }

    @Override
    public boolean init() throws IOException {
        if (Files.notExists(this.h2File.getParent())) {
            Files.createDirectories(this.h2File.getParent());
        }

        try {
            this.connection = DriverManager.getConnection("jdbc:h2:" + this.h2File.toAbsolutePath());
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public int executeUpdate(String query, Object... objects) {
        Preconditions.checkNotNull(query);
        Preconditions.checkNotNull(objects);

        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(query)) {
            int i = 1;
            for (Object object : objects) {
                preparedStatement.setString(i++, object.toString());
            }

            return preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return -1;
    }

    @Override
    public <T> T executeQuery(String query, ThrowableFunction<ResultSet, T, SQLException> function, Object... objects) {
        Preconditions.checkNotNull(query);
        Preconditions.checkNotNull(function);
        Preconditions.checkNotNull(objects);

        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(query)) {
            int i = 1;
            for (Object object : objects) {
                preparedStatement.setString(i++, object.toString());
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return function.apply(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean deleteDatabase(String name) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("DROP TABLE IF EXISTS `" + name + "`")) {
            return preparedStatement.executeUpdate() != -1;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    @Override
    public Collection<String> getDatabaseNames() {
        return this.executeQuery(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'",
                resultSet -> {
                    Collection<String> collection = new ArrayList<>();
                    while (resultSet.next()) {
                        collection.add(resultSet.getString("table_name"));
                    }

                    return collection;
                }
        );
    }

    @Override
    public void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}
