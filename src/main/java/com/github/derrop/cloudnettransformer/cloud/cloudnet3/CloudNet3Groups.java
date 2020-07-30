package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceDeployment;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceEnvironment;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceGroup;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTemplate;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "Groups", priority = ExecutorPriority.FIRST)
public class CloudNet3Groups implements CloudReaderWriter {

    private Path groupsPath(Path directory) {
        return directory.resolve("local").resolve("groups.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {

        Collection<Document> groups = new ArrayList<>();

        for (ServiceGroup group : cloudSystem.getGroups()) {
            Document document = Documents.newDocument();

            document.append("name", group.getName())
                    .append("jvmOptions", group.getJvmOptions())
                    .append("targetEnvironments", group.getEnvironments())
                    .append("includes", group.getInclusions().stream().map(CloudNet3Utils::inclusionToDocument).collect(Collectors.toList()))
                    .append("templates", group.getTemplates())
                    .append("deployments", group.getDeployments())
                    .append("properties", Documents.newDocument());
        }

        Documents.jsonStorage().write(Documents.newDocument("groups", groups), this.groupsPath(directory));

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
        }

        return true;
    }
}
