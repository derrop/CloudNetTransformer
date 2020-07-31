package com.github.derrop.cloudnettransformer.cloudnet2.database;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import com.github.derrop.cloudnettransformer.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CloudNet2FileDatabaseProvider implements DatabaseProvider {

    private final Path directory;

    public CloudNet2FileDatabaseProvider(Path directory) {
        this.directory = directory;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public Database getDatabase(String name) {
        return new CloudNet2FileDatabase(this.directory.resolve(name), name);
    }

    @Override
    public boolean containsDatabase(String name) {
        return Files.exists(this.directory.resolve(name));
    }

    @Override
    public boolean deleteDatabase(String name) {
        Path path = this.directory.resolve(name);
        if (Files.exists(path)) {
            try {
                FileUtils.deleteDirectory(path);
                return true;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Collection<String> getDatabaseNames() {
        try {
            return Files.list(this.directory).map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public void close() {
    }
}
