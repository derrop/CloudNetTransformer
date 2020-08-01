package com.github.derrop.cloudnettransformer.cloudnet3.service;

import com.github.derrop.cloudnettransformer.cloud.CloudNetTemplates;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory.TemplateDirectory;
import com.github.derrop.cloudnettransformer.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

public class CloudNet3Templates extends CloudNetTemplates {

    @Override
    protected Path templatesDirectory(Path directory) {
        return directory.resolve("local").resolve("templates");
    }

    @Override
    protected TemplateDirectory createTemplateDirectory(String group, String name, Path directory) {
        return new TemplateDirectory(group, name, directory) {
            @Override
            public void copyTo(Path targetDirectory) throws IOException {
                if (super.getDirectory() == null) {
                    return;
                }
                FileUtils.copyDirectory(super.getDirectory(), targetDirectory, path -> {
                    String name = path.getFileName().toString();
                    if (!super.getDirectory().relativize(path).startsWith(name)) {
                        return name;
                    }

                    if (!name.endsWith(".jar")) {
                        return name;
                    }
                    return mapFileName(name, "spigot.jar", "minecraft", "spigot", "paper", "taco", "akarin");
                });
            }
        };
    }

    private String mapFileName(String original, String replacement, String... possibilities) {
        for (String possibility : possibilities) {
            if (original.toLowerCase().contains(possibility.toLowerCase())) {
                return replacement;
            }
        }
        return original;
    }

}
