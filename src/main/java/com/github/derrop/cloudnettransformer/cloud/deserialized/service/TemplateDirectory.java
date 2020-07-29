package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

import com.github.derrop.cloudnettransformer.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

public class TemplateDirectory {

    private final String prefix;
    private final String name;
    private final Path directory;

    public TemplateDirectory(String prefix, String name, Path directory) {
        this.prefix = prefix;
        this.name = name;
        this.directory = directory;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getName() {
        return this.name;
    }

    public Path getDirectory() {
        return this.directory;
    }

    public void copyTo(Path targetDirectory) throws IOException {
        FileUtils.copyDirectory(this.directory, targetDirectory);
    }

}
