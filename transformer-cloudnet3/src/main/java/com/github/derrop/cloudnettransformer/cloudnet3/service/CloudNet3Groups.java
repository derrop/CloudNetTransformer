package com.github.derrop.cloudnettransformer.cloudnet3.service;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceDeployment;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceEnvironment;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceGroup;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTemplate;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory.TemplateDirectory;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.cloudnettransformer.cloudnet3.CloudNet3Utils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "Groups", priority = ExecutorPriority.FIRST)
public class CloudNet3Groups implements CloudReaderWriter {

    private Path groupsPath(Path directory) {
        return directory.resolve("local").resolve("groups.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {

        Collection<ServiceGroup> groups = new ArrayList<>(cloudSystem.getGroups());

        for (ServiceEnvironment environment : ServiceEnvironment.values()) {
            String name = environment == ServiceEnvironment.BUNGEECORD ? Constants.GLOBAL_PROXY_GROUP_SUFFIX : environment == ServiceEnvironment.MINECRAFT_SERVER ? Constants.GLOBAL_SERVER_GROUP_SUFFIX : environment.toString();
            ServiceTemplate template = new ServiceTemplate("local", "Global", name.toLowerCase());

            if (cloudSystem.getGlobalTemplates(environment).isEmpty()) {
                cloudSystem.addGlobalTemplate(environment, new TemplateDirectory(template.getPrefix(), template.getName(), null));
            }

            for (TemplateDirectory globalTemplate : cloudSystem.getGlobalTemplates(environment)) {
                groups.add(new ServiceGroup("Global-" + name, Collections.singletonList(template), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.singletonList(environment)));

                Path targetDirectory = directory.resolve("local").resolve("templates").resolve(template.getPrefix()).resolve(template.getName());
                Files.createDirectories(targetDirectory);

                if (environment == ServiceEnvironment.MINECRAFT_SERVER) {
                    CloudNet3Utils.copyExtraSpigotFiles(targetDirectory);
                }
                globalTemplate.copyTo(targetDirectory);

                Path applicationFile = cloudSystem.getApplicationFiles().get(environment);
                if (applicationFile != null) {
                    Files.copy(applicationFile, targetDirectory.resolve(applicationFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        Collection<Document> documents = new ArrayList<>();

        for (ServiceGroup group : groups) {
            Document document = Documents.newDocument()
                    .append("name", group.getName())
                    .append("jvmOptions", group.getJvmOptions())
                    .append("targetEnvironments", group.getEnvironments())
                    .append("includes", group.getInclusions().stream().map(CloudNet3Utils::inclusionToDocument).collect(Collectors.toList()))
                    .append("templates", group.getTemplates())
                    .append("deployments", group.getDeployments())
                    .append("properties", Documents.newDocument());

            documents.add(document);
        }

        Documents.jsonStorage().write(Documents.newDocument("groups", documents), this.groupsPath(directory));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {

        Path path = this.groupsPath(directory);
        if (Files.notExists(path)) {
            return true;
        }

        Collection<Document> groups = Documents.jsonStorage().read(path).getDocuments("groups");
        if (groups == null) {
            return false;
        }

        for (Document group : groups) {
            ServiceGroup serviceGroup = new ServiceGroup(
                    group.getString("name"),
                    group.get("templates", TypeToken.getParameterized(Collection.class, ServiceTemplate.class).getType()),
                    group.getDocuments("includes").stream().map(CloudNet3Utils::documentToInclusion).collect(Collectors.toList()),
                    group.get("deployments", TypeToken.getParameterized(Collection.class, ServiceDeployment.class).getType()),
                    group.get("jvmOptions", TypeToken.getParameterized(Collection.class, String.class).getType()),
                    group.get("targetEnvironments", TypeToken.getParameterized(Collection.class, ServiceEnvironment.class).getType())
            );
            serviceGroup.getEnvironments().removeIf(Objects::isNull);
            cloudSystem.getGroups().add(serviceGroup);

            for (ServiceEnvironment environment : serviceGroup.getEnvironments()) {
                for (ServiceTemplate template : serviceGroup.getTemplates()) {
                    TemplateDirectory templateDirectory = new TemplateDirectory(
                            template.getPrefix(), template.getName(),
                            directory.resolve("local").resolve("templates").resolve(template.getPrefix()).resolve(template.getName())
                    );
                    cloudSystem.addGlobalTemplate(environment, templateDirectory);

                    if (cloudSystem.getApplicationFiles().containsKey(environment)) {
                        continue;
                    }
                    Path applicationFile = CloudNet3Utils.resolveApplicationPath(environment, templateDirectory.getDirectory());
                    if (applicationFile != null) {
                        cloudSystem.getApplicationFiles().put(environment, applicationFile);
                    }
                }
            }
        }

        return true;
    }
}
