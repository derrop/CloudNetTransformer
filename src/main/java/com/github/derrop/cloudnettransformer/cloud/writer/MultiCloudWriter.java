package com.github.derrop.cloudnettransformer.cloud.writer;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class MultiCloudWriter implements CloudWriter {

    private final Collection<CloudWriter> writers;

    public MultiCloudWriter(Collection<CloudWriter> writers) {
        this.writers = writers;
    }

    @Override
    public String getName() {
        return this.writers.stream().map(CloudWriter::getName).collect(Collectors.joining(", "));
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        for (CloudWriter writer : this.writers) {
            System.out.println("Writing '" + writer.getName() + "'...");
            if (!writer.write(cloudSystem, directory)) {
                System.err.println("Failed to write '" + writer.getName() + "'");
                return false;
            }
            System.out.println("Successfully wrote '" + writer.getName() + "'");
        }
        return true;
    }
}
