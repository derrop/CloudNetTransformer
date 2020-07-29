package com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory;

import java.nio.file.Path;

public class StaticServiceDirectory extends ServiceDirectory {

    private final String task;
    private final int id;

    public StaticServiceDirectory(String task, int id, Path directory, String... excludedFiles) {
        super(directory, excludedFiles);
        this.task = task;
        this.id = id;
    }

    public String getTask() {
        return this.task;
    }

    public int getId() {
        return this.id;
    }

}
