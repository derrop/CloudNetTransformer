package com.github.derrop.cloudnettransformer.cloudnet2.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.util.FileUtils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class CloudNet2FileDatabase implements Database {

    private static final String UNIQUE_NAME_KEY = "_database_id_unique";

    private final Path directory;
    private final String name;

    public CloudNet2FileDatabase(Path directory, String name) {
        this.directory = directory;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private Document prepareDocument(String key, Document document) {
        return document.append(UNIQUE_NAME_KEY, key);
    }

    @Override
    public boolean insert(String key, Document document) {
        Path path = this.directory.resolve(key);
        if (Files.exists(path)) {
            return false;
        }

        Documents.jsonStorage().write(this.prepareDocument(key, document), path);
        return true;
    }

    @Override
    public boolean update(String key, Document document) {
        Path path = this.directory.resolve(key);
        if (Files.notExists(path)) {
            return false;
        }

        Documents.jsonStorage().write(this.prepareDocument(key, document), path);
        return true;
    }

    @Override
    public boolean contains(String key) {
        return Files.exists(this.directory.resolve(key));
    }

    @Override
    public boolean delete(String key) {
        try {
            return Files.deleteIfExists(this.directory.resolve(key));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public Document get(String key) {
        return Documents.jsonStorage().read(this.directory.resolve(key));
    }

    @Override
    public List<Document> get(String fieldName, Object fieldValue) {
        return new ArrayList<>(this.filter((key, document) -> Objects.equals(document.getString(fieldName), fieldValue)).values());
    }

    @Override
    public List<Document> get(Document filters) {
        return new ArrayList<>(this.filter((s, document) -> {
            for (String key : filters.keys()) {
                if (document.get(key) != null && document.get(key).equals(filters.get(key))) {
                    return true;
                }
            }
            return false;
        }).values());
    }

    @Override
    public Collection<String> keys() {
        try {
            return Files.list(this.directory).map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<Document> documents() {
        try {
            return Files.list(this.directory).map(path -> Documents.jsonStorage().read(path)).collect(Collectors.toList());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Document> entries() {
        return this.filter((key, document) -> true);
    }

    @Override
    public Map<String, Document> filter(BiPredicate<String, Document> predicate) {
        Map<String, Document> map = new HashMap<>();
        this.iterate((key, document) -> {
            if (predicate.test(key, document)) {
                map.put(key, document);
            }
        });
        return map;
    }

    @Override
    public void iterate(BiConsumer<String, Document> consumer) {
        try {
            Files.list(this.directory).forEach(path -> {
                String key = path.getFileName().toString();
                Document document = Documents.jsonStorage().read(path);
                consumer.accept(key, document);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            FileUtils.deleteDirectory(this.directory);
            Files.createDirectory(this.directory);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public long getDocumentsCount() {
        try {
            return Files.list(this.directory).count();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

}
