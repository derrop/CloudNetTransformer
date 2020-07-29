package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.CloudNetTemplates;

import java.nio.file.Path;

public class CloudNet3Templates extends CloudNetTemplates {

    @Override
    protected Path templatesDirectory(Path directory) {
        return directory.resolve("local").resolve("templates");
    }

}
