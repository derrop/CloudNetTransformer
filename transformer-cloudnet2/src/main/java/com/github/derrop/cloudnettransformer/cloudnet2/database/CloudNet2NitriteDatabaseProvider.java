package com.github.derrop.cloudnettransformer.cloudnet2.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import org.dizitart.no2.Nitrite;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class CloudNet2NitriteDatabaseProvider implements DatabaseProvider {

    protected static final String TYPE_PREFIX = DatabaseDocument.class.getName() + "+";

    private final Path path;

    private Nitrite nitrite;

    public CloudNet2NitriteDatabaseProvider(Path path) {
        this.path = path;
    }

    @Override
    public boolean init() {
        this.nitrite = Nitrite.builder()
                .filePath(this.path.toFile())
                .openOrCreate();

        return true;
    }

    @Override
    public Database getDatabase(String name) {
        return new CloudNet2NitriteDatabase(name, this.nitrite);
    }

    @Override
    public boolean containsDatabase(String name) {
        return this.nitrite.hasRepository(name, DatabaseDocument.class);
    }

    @Override
    public boolean deleteDatabase(String name) {
        if (!this.nitrite.hasRepository(name, DatabaseDocument.class)) {
            return false;
        }
        this.nitrite.getRepository(name, DatabaseDocument.class).drop();
        return true;
    }

    @Override
    public Collection<String> getDatabaseNames() {
        return this.nitrite.listRepositories().stream()
                .filter(s -> s.startsWith(TYPE_PREFIX))
                .map(s -> s.substring(TYPE_PREFIX.length()))
                .collect(Collectors.toList());
    }

    @Override
    public void close() {
        if (this.nitrite != null) {
            this.nitrite.close();
        }
    }
}
