package com.github.derrop.cloudnettransformer.cloud.writer;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;

import java.io.IOException;
import java.nio.file.Path;

public interface CloudWriter {

    String getName();

    boolean write(CloudSystem cloudSystem, Path directory) throws IOException;

}
