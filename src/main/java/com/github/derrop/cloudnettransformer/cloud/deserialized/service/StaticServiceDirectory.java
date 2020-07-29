package com.github.derrop.cloudnettransformer.cloud.deserialized.service;

import com.github.derrop.cloudnettransformer.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

public class StaticServiceDirectory {

    private final String task;
    private final int id;
    private final Path directory;

    public StaticServiceDirectory(String task, int id, Path directory) {
        this.task = task;
        this.id = id;
        this.directory = directory;
    }

    public String getTask() {
        return this.task;
    }

    public int getId() {
        return this.id;
    }

    public Path getDirectory() {
        return this.directory;
    }

    public void copyTo(Path targetDirectory) throws IOException {
        FileUtils.copyDirectory(this.directory, targetDirectory);
    }

}
