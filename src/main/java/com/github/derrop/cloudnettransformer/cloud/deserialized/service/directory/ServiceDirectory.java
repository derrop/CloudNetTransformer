package com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory;

import com.github.derrop.cloudnettransformer.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class ServiceDirectory {

    private final Path directory;
    private final Collection<String> excludedFiles;

    public ServiceDirectory(Path directory, String[] excludedFiles) {
        this(directory, new ArrayList<>(Arrays.asList(excludedFiles)));
    }

    public ServiceDirectory(Path directory, Collection<String> excludedFiles) {
        this.directory = directory;
        this.excludedFiles = excludedFiles;
    }

    public Path getDirectory() {
        return this.directory;
    }

    public Collection<String> getExcludedFiles() {
        return this.excludedFiles;
    }

    public void copyTo(Path targetDirectory) throws IOException {
        FileUtils.copyDirectory(this.directory, targetDirectory, this.excludedFiles.toArray(new String[0]));
    }

}
