package com.github.derrop.cloudnettransformer.cloudnet2.files.server;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloudnet2.CloudNet2Templates;

import java.nio.file.Path;

public class CloudNet2WrapperTemplates extends CloudNet2Templates {

    @Override
    protected Path templatesDirectory(Path directory) {
        return directory.resolve(Constants.WRAPPER_DIRECTORY).resolve("local").resolve("templates");
    }

}
