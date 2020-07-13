package com.github.derrop.cloudnettransformer.cloud.cloudnet3.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.common.base.Preconditions;

import java.sql.ResultSet;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class CloudNet3SQLDatabase implements Database {

    private static final String TABLE_COLUMN_KEY = "Name", TABLE_COLUMN_VALUE = "Document";

    protected final CloudNet3SQLDatabaseProvider databaseProvider;
    protected final String name;

    public CloudNet3SQLDatabase(CloudNet3SQLDatabaseProvider databaseProvider, String name) {
        Preconditions.checkNotNull(databaseProvider);
        Preconditions.checkNotNull(name);

        this.databaseProvider = databaseProvider;
        this.name = name;

        databaseProvider.executeUpdate("CREATE TABLE IF NOT EXISTS `" + name + "` (" + TABLE_COLUMN_KEY + " VARCHAR(1024), " + TABLE_COLUMN_VALUE + " TEXT);");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean insert(String key, Document document) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(document);

        return !this.contains(key) && this.insert0(key, document);
    }

    private boolean insert0(String key, Document document) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(document);

        return this.databaseProvider.executeUpdate(
                "INSERT INTO `" + this.name + "` (" + TABLE_COLUMN_KEY + "," + TABLE_COLUMN_VALUE + ") VALUES (?, ?);",
                key, document.toString()
        ) != -1;
    }

    @Override
    public boolean update(String key, Document document) {
        return this.contains(key) && this.update0(key, document);
    }

    private boolean update0(String key, Document document) {
        return this.databaseProvider.executeUpdate(
                "UPDATE `" + this.name + "` SET " + TABLE_COLUMN_VALUE + "=? WHERE " + TABLE_COLUMN_KEY + "=?",
                document.toString(), key
        ) != -1;
    }

    @Override
    public boolean contains(String key) {
        Preconditions.checkNotNull(key);

        return this.databaseProvider.executeQuery(
                "SELECT " + TABLE_COLUMN_KEY + " FROM `" + this.name + "` WHERE " + TABLE_COLUMN_KEY + "=?",
                ResultSet::next,
                key
        );
    }

    @Override
    public boolean delete(String key) {
        Preconditions.checkNotNull(key);

        return this.databaseProvider.executeUpdate(
                "DELETE FROM `" + this.name + "` WHERE " + TABLE_COLUMN_KEY + "=?",
                key
        ) != -1;
    }

    @Override
    public Document get(String key) {
        Preconditions.checkNotNull(key);

        return this.databaseProvider.executeQuery(
                "SELECT " + TABLE_COLUMN_VALUE + " FROM `" + this.name + "` WHERE " + TABLE_COLUMN_KEY + "=?",
                resultSet -> resultSet.next() ? Documents.jsonStorage().read(resultSet.getString(TABLE_COLUMN_VALUE)) : null,
                key
        );
    }

    @Override
    public List<Document> get(String fieldName, Object fieldValue) {
        Preconditions.checkNotNull(fieldName);
        Preconditions.checkNotNull(fieldValue);

        return this.databaseProvider.executeQuery(
                "SELECT " + TABLE_COLUMN_VALUE + " FROM `" + this.name + "` WHERE " + TABLE_COLUMN_VALUE + " LIKE ?",
                resultSet -> {
                    List<Document> documents = new ArrayList<>();

                    while (resultSet.next()) {
                        documents.add(Documents.jsonStorage().read(resultSet.getString(TABLE_COLUMN_VALUE)));
                    }

                    return documents;
                },
                "%\"" + fieldName + "\":" + Documents.jsonStorage().toString(Documents.newDocument(fieldValue)) + "%"
        );
    }

    @Override
    public List<Document> get(Document filters) {
        Preconditions.checkNotNull(filters);

        StringBuilder stringBuilder = new StringBuilder("SELECT ").append(TABLE_COLUMN_VALUE).append(" FROM `").append(this.name).append('`');

        Collection<String> collection = new ArrayList<>();

        if (filters.size() > 0) {
            stringBuilder.append(" WHERE ");

            Iterator<String> iterator = filters.keys().iterator();
            String item;

            while (iterator.hasNext()) {
                item = iterator.next();

                stringBuilder.append(TABLE_COLUMN_VALUE).append(" LIKE ?");
                collection.add("%\"" + item + "\":" + filters.get(item).toString() + "%");

                if (iterator.hasNext()) {
                    stringBuilder.append(" and ");
                }
            }
        }

        return this.databaseProvider.executeQuery(
                stringBuilder.toString(),
                resultSet -> {
                    List<Document> documents = new ArrayList<>();

                    while (resultSet.next()) {
                        documents.add(Documents.jsonStorage().read(resultSet.getString(TABLE_COLUMN_VALUE)));
                    }

                    return documents;
                },
                collection.toArray()
        );
    }

    @Override
    public Collection<String> keys() {
        return this.databaseProvider.executeQuery(
                "SELECT " + TABLE_COLUMN_KEY + " FROM `" + this.name + "`",
                resultSet -> {
                    Collection<String> keys = new ArrayList<>();

                    while (resultSet.next()) {
                        keys.add(resultSet.getString(TABLE_COLUMN_KEY));
                    }

                    return keys;
                }
        );
    }

    @Override
    public Collection<Document> documents() {
        return this.databaseProvider.executeQuery(
                "SELECT " + TABLE_COLUMN_VALUE + " FROM `" + this.name + "`",
                resultSet -> {
                    Collection<Document> documents = new ArrayList<>();

                    while (resultSet.next()) {
                        documents.add(Documents.jsonStorage().read(resultSet.getString(TABLE_COLUMN_VALUE)));
                    }

                    return documents;
                }
        );
    }

    @Override
    public Map<String, Document> entries() {
        return this.databaseProvider.executeQuery(
                "SELECT * FROM `" + this.name + "`",
                resultSet -> {
                    Map<String, Document> map = new WeakHashMap<>();

                    while (resultSet.next()) {
                        map.put(resultSet.getString(TABLE_COLUMN_KEY), Documents.jsonStorage().read(resultSet.getString(TABLE_COLUMN_VALUE)));
                    }

                    return map;
                }
        );
    }

    @Override
    public Map<String, Document> filter(BiPredicate<String, Document> predicate) {
        Preconditions.checkNotNull(predicate);

        return this.databaseProvider.executeQuery(
                "SELECT * FROM `" + this.name + "`",
                resultSet -> {
                    Map<String, Document> map = new HashMap<>();

                    while (resultSet.next()) {
                        String key = resultSet.getString(TABLE_COLUMN_KEY);
                        Document document = Documents.jsonStorage().read(resultSet.getString(TABLE_COLUMN_VALUE));

                        if (predicate.test(key, document)) {
                            map.put(key, document);
                        }
                    }

                    return map;
                }
        );
    }

    @Override
    public void iterate(BiConsumer<String, Document> consumer) {
        Preconditions.checkNotNull(consumer);

        this.databaseProvider.executeQuery(
                "SELECT * FROM `" + this.name + "`",
                resultSet -> {
                    while (resultSet.next()) {
                        String key = resultSet.getString(TABLE_COLUMN_KEY);
                        Document document = Documents.jsonStorage().read(resultSet.getString(TABLE_COLUMN_VALUE));
                        consumer.accept(key, document);
                    }

                    return null;
                }
        );
    }

    @Override
    public void clear() {
        this.databaseProvider.executeUpdate("TRUNCATE TABLE `" + this.name + "`");
    }

    @Override
    public long getDocumentsCount() {
        return this.databaseProvider.executeQuery("SELECT COUNT(*) FROM " + this.name, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return -1L;
        });
    }

}
