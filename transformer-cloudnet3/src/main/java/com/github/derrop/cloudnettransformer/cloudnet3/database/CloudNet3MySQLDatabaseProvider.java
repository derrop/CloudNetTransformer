package com.github.derrop.cloudnettransformer.cloudnet3.database;

import com.github.derrop.cloudnettransformer.util.ThrowableFunction;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariDataSource;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class CloudNet3MySQLDatabaseProvider extends CloudNet3SQLDatabaseProvider {

    private final Path directory;
    private final HikariDataSource hikariDataSource = new HikariDataSource();

    public CloudNet3MySQLDatabaseProvider(Path directory) {
        this.directory = directory;
    }

    @Override
    public boolean init() {
        Path path = this.directory.resolve("modules").resolve("CloudNet-Database-MySQL").resolve("config.json");
        Document config = Documents.jsonStorage().read(path);

        Collection<Document> addresses = config.getDocuments("addresses");
        if (addresses == null || addresses.isEmpty()) {
            System.err.println("No addresses in the MySQL configuration found");
            return false;
        }

        Document address = addresses.iterator().next();
        Document host = address.getDocument("address");

        this.hikariDataSource.setJdbcUrl("jdbc:mysql://" + host.getString("host") + ":" + host.getInt("port") + "/" + address.getString("database") +
                String.format("?useSSL=%b&trustServerCertificate=%b", address.getBoolean("useSsl"), address.getBoolean("useSsl"))
        );

        //base configuration
        this.hikariDataSource.setUsername(config.getString("username"));
        this.hikariDataSource.setPassword(config.getString("password"));
        this.hikariDataSource.setDriverClassName("com.mysql.jdbc.Driver");

        this.hikariDataSource.setMaximumPoolSize(config.getInt("connectionPoolSize"));
        this.hikariDataSource.setConnectionTimeout(config.getInt("connectionTimeout"));
        this.hikariDataSource.setValidationTimeout(config.getInt("validationTimeout"));

        this.hikariDataSource.validate();
        return true;
    }

    @Override
    public boolean deleteDatabase(String name) {
        Preconditions.checkNotNull(name);

        if (this.containsDatabase(name)) {
            try (Connection connection = this.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE " + name)) {
                return preparedStatement.executeUpdate() != -1;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
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
        this.hikariDataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    @Override
    public int executeUpdate(String query, Object... objects) {
        Preconditions.checkNotNull(query);
        Preconditions.checkNotNull(objects);

        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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

        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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
}
