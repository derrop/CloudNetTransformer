package com.github.derrop.cloudnettransformer.cloud.deserialized.database;

import com.github.derrop.documents.Document;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public interface Database {

    String getName();

    boolean insert(String key, Document document);

    boolean update(String key, Document document);

    boolean contains(String key);

    boolean delete(String key);

    Document get(String key);

    List<Document> get(String fieldName, Object fieldValue);

    List<Document> get(Document filters);

    Collection<String> keys();

    Collection<Document> documents();

    Map<String, Document> entries();

    Map<String, Document> filter(BiPredicate<String, Document> predicate);

    void iterate(BiConsumer<String, Document> consumer);

    void clear();

    long getDocumentsCount();

}
