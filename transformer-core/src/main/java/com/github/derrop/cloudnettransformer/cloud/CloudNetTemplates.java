package com.github.derrop.cloudnettransformer.cloud;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory.TemplateDirectory;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@DescribedCloudExecutor(name = "Templates", priority = ExecutorPriority.FIRST)
public abstract class CloudNetTemplates implements CloudReaderWriter {

    protected abstract Path templatesDirectory(Path directory);

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        this.writeTemplates(this.templatesDirectory(directory), cloudSystem.getTemplates());
        return true;
    }

    protected TemplateDirectory createTemplateDirectory(String group, String name, Path directory) {
        return new TemplateDirectory(group, name, directory);
    }

    protected void writeTemplates(Path directory, Collection<TemplateDirectory> templates) throws IOException {
        for (TemplateDirectory template : templates) {
            Path templateDirectory = directory.resolve(template.getPrefix()).resolve(template.getName());

            template.copyTo(templateDirectory);
        }
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
                        if (!Files.isDirectory(prefixDirectory) || !Files.isDirectory(nameDirectory)) {
                            continue;
                        }
                        String group = prefixDirectory.getFileName().toString();
                        String name = nameDirectory.getFileName().toString();

                        cloudSystem.getTemplates().add(this.createTemplateDirectory(group, name, nameDirectory));
                    }
                }
            }
        }

        return true;
    }

}
