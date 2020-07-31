package com.github.derrop.cloudnettransformer.cloudnet2.files.server;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.CloudNetTemplates;

import java.nio.file.Path;

public class CloudNet2MasterTemplates extends CloudNetTemplates {

    // TODO bungee templates in CloudNet 2 don't have a name, only a prefix

    @Override
    protected Path templatesDirectory(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("local").resolve("templates");
    }


}
