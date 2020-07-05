package com.github.derrop.cloudnettransformer.cloud.reader;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;

import java.io.IOException;
import java.nio.file.Path;

public interface CloudReader {

    String getName();

    boolean read(CloudSystem cloudSystem, Path directory) throws IOException;

}
