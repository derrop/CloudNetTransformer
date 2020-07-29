package com.github.derrop.cloudnettransformer.cloud;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.TemplateDirectory;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "Templates")
public abstract class CloudNetTemplates implements CloudReaderWriter {

    protected abstract Path templatesDirectory(Path directory);

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        Path templates = this.templatesDirectory(directory);

        for (TemplateDirectory template : cloudSystem.getTemplates()) {
            Path templateDirectory = templates.resolve(template.getPrefix()).resolve(template.getName());

            template.copyTo(templateDirectory);
        }

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        Path templates = this.templatesDirectory(directory);
        if (Files.notExists(templates)) {
            return true;
        }

        try (DirectoryStream<Path> prefixStream = Files.newDirectoryStream(templates)) {
            for (Path prefixDirectory : prefixStream) {
                try (DirectoryStream<Path> nameStream = Files.newDirectoryStream(prefixDirectory)) {
                    for (Path nameDirectory : nameStream) {
                        String group = prefixDirectory.getFileName().toString();
                        String name = nameDirectory.getFileName().toString();

                        cloudSystem.getTemplates().add(new TemplateDirectory(group, name, nameDirectory));
                    }
                }
            }
        }

        return true;
    }

}
