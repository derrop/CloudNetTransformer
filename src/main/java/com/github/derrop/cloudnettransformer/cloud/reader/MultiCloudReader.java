package com.github.derrop.cloudnettransformer.cloud.reader;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class MultiCloudReader implements CloudReader {

    private final Collection<CloudReader> readers;

    public MultiCloudReader(Collection<CloudReader> readers) {
        this.readers = readers;
    }

    @Override
    public String getName() {
        return this.readers.stream().map(CloudReader::getName).collect(Collectors.joining(", "));
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        for (CloudReader reader : this.readers) {
            System.out.println("Reading '" + reader.getName() + "'...");
            if (!reader.read(cloudSystem, directory)) {
                System.err.println("Failed to read '" + reader.getName() + "'");
                return false;
            }
            System.out.println("Successfully read '" + reader.getName() + "'");
        }
        return true;
    }
}
