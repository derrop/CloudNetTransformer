package com.github.derrop.cloudnettransformer.cloud.deserialized.database;

import java.io.IOException;
import java.util.Collection;

public interface DatabaseProvider {

    boolean init() throws IOException;

    Database getDatabase(String name);

    boolean containsDatabase(String name);

    boolean deleteDatabase(String name);

    Collection<String> getDatabaseNames();

    void close();

}
