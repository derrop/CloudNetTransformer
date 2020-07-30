package com.github.derrop.cloudnettransformer.cloud.cloudnet3.service;

import com.github.derrop.cloudnettransformer.cloud.CloudNetTemplates;

import java.nio.file.Path;

public class CloudNet3Templates extends CloudNetTemplates {

    // TODO replace paper.jar with spigot.jar

    @Override
    protected Path templatesDirectory(Path directory) {
        return directory.resolve("local").resolve("templates");
    }

}
