package com.github.derrop.cloudnettransformer.cloud.cloudnet2;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.CloudNetTemplates;

import java.nio.file.Path;

public class CloudNet2Templates extends CloudNetTemplates {

    @Override
    protected Path templatesDirectory(Path directory) {
        return directory.resolve(Constants.WRAPPER_DIRECTORY).resolve("local").resolve("templates");
    }


}
