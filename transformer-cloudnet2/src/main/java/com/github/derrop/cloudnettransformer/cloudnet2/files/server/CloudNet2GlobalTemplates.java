package com.github.derrop.cloudnettransformer.cloudnet2.files.server;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.CloudNetTemplates;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceEnvironment;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTask;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTemplate;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory.TemplateDirectory;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.cloudnettransformer.util.FileUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

@DescribedCloudExecutor(name = "GlobalTemplate", priority = ExecutorPriority.LAST + 1)
public class CloudNet2GlobalTemplates extends CloudNetTemplates {

    private Path localDirectory(Path directory) {
        return directory.resolve(Constants.WRAPPER_DIRECTORY).resolve("local");
    }

    private Path globalDirectory(Path directory) {
        return this.localDirectory(directory).resolve("global");
    }

    @Override
    protected Path templatesDirectory(Path directory) {
        return this.localDirectory(directory).resolve("templates");
    }

    @Override
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) throws IOException {
        for (TemplateDirectory template : cloudSystem.getGlobalTemplates(ServiceEnvironment.MINECRAFT_SERVER)) {
            template.copyTo(this.globalDirectory(directory));

            FileUtils.deleteDirectory(this.templatesDirectory(directory).resolve(template.getPrefix()).resolve(template.getName()));
        }

        for (TemplateDirectory template : cloudSystem.getGlobalTemplates(ServiceEnvironment.BUNGEECORD)) {
            for (ServiceTask task : cloudSystem.getTasks()) {
                if (task.getEnvironment() != ServiceEnvironment.BUNGEECORD || task.getTemplates().isEmpty()) {
                    continue;
                }

                ServiceTemplate mainTemplate = task.getTemplates().iterator().next();

                template.copyTo(this.templatesDirectory(directory).resolve(mainTemplate.getPrefix()));
            }
        }

        for (ServiceEnvironment environment : ServiceEnvironment.values()) {
            Path applicationFile = cloudSystem.getApplicationFiles().get(environment);
            if (applicationFile == null) {
                continue;
            }

            switch (environment) {
                case MINECRAFT_SERVER:
                    Files.copy(applicationFile, this.localDirectory(directory).resolve("spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
                    break;
                case BUNGEECORD:
                    Path proxyVersions = this.localDirectory(directory).resolve("proxy_versions");
                    Files.createDirectories(proxyVersions);
                    Files.copy(applicationFile, proxyVersions.resolve("BungeeCord.jar"), StandardCopyOption.REPLACE_EXISTING);
                    break;
            }
        }

        return ExecuteResult.success();
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) throws IOException {
        cloudSystem.addGlobalTemplate(ServiceEnvironment.MINECRAFT_SERVER, new TemplateDirectory(
                "Global", "server",
                this.globalDirectory(directory)
        ));

        cloudSystem.getApplicationFiles().put(ServiceEnvironment.MINECRAFT_SERVER, this.localDirectory(directory).resolve("spigot.jar"));
        Path proxyVersionsPath = this.localDirectory(directory).resolve("proxy_versions");
        if (Files.exists(proxyVersionsPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(proxyVersionsPath)) {
                Iterator<Path> iterator = stream.iterator();
                if (iterator.hasNext()) {
                    cloudSystem.getApplicationFiles().put(ServiceEnvironment.BUNGEECORD, iterator.next());
                }
            }
        }

        return ExecuteResult.success();
    }
}
