package com.github.derrop.cloudnettransformer.cloudnet2.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.documents.Document;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import org.dizitart.no2.IndexOptions;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class CloudNet2NitriteDatabase implements Database {

    public static final String UNIQUE_NAME_KEY = "_database_id_unique";

    private final String name;
    private final Nitrite nitrite;
    private final ObjectRepository<DatabaseDocument> repository;

    public CloudNet2NitriteDatabase(String name, Nitrite nitrite) {
        this.name = name;
        this.nitrite = nitrite;
        this.repository = this.nitrite.getRepository(this.name, DatabaseDocument.class);
        if (!this.repository.hasIndex(UNIQUE_NAME_KEY)) {
            this.repository.createIndex(UNIQUE_NAME_KEY, IndexOptions.indexOptions(IndexType.Unique));
        }
    }

    private DatabaseDocument prepareDocument(String key, Document document) {
        return new DatabaseDocument(document, key);
    }

    private Document map(DatabaseDocument document) {
        return document == null ? null : document.getBackingDocument();
    }

    private List<Document> map(List<DatabaseDocument> documents) {
        return documents.stream().map(this::map).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean insert(String key, Document document) {
        if (this.repository.update(this.prepareDocument(key, document), true).getAffectedCount() > 0) {
            this.nitrite.commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean update(String key, Document document) {
        if (this.repository.update(this.prepareDocument(key, document), false).getAffectedCount() > 0) {
            this.nitrite.commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(String key) {
        return this.get(key) != null;
    }

    @Override
    public boolean delete(String key) {
        return this.repository.remove(ObjectFilters.eq(UNIQUE_NAME_KEY, key)).getAffectedCount() > 0;
    }

    @Override
    public Document get(String key) {
        return this.map(this.repository.find(ObjectFilters.eq(UNIQUE_NAME_KEY, key)).firstOrDefault());
    }

    @Override
    public List<Document> get(String fieldName, Object fieldValue) {
        return this.repository.find(ObjectFilters.eq(fieldName, fieldValue)).toList().stream().map(this::map).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Document> get(Document filters) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Collection<String> keys() {
        return this.repository.find().toList().stream().map(document -> document.getId()).collect(Collectors.toList());
    }

    @Override
    public Collection<Document> documents() {
        return this.map(this.repository.find().toList());
    }

    @Override
    public Map<String, Document> entries() {
        return this.repository.find().toList().stream().collect(Collectors.toMap(DatabaseDocument::getId, DatabaseDocument::getBackingDocument));
    }

    @Override
    public Map<String, Document> filter(BiPredicate<String, Document> predicate) {
        Map<String, Document> result = new HashMap<>();
        this.iterate((key, document) -> {
            if (predicate.test(key, document)) {
                result.put(key, document);
            }
        });
        return result;
    }

    @Override
    public void iterate(BiConsumer<String, Document> consumer) {
        this.repository.find().forEach(document -> consumer.accept(document.getId(), document.getBackingDocument()));
    }

    @Override
    public void clear() {
        this.repository.remove(ObjectFilters.ALL);
    }

    @Override
    public long getDocumentsCount() {
        return this.repository.size();
    }
}
