package com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory;

import java.nio.file.Path;

public class TemplateDirectory extends ServiceDirectory {

    private final String prefix;
    private final String name;

    public TemplateDirectory(String prefix, String name, Path directory, String... excludedFiles) {
        super(directory, excludedFiles);
        this.prefix = prefix;
        this.name = name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getName() {
        return this.name;
    }

}
