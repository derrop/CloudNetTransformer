package com.github.derrop.cloudnettransformer.cloudnet2;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceInclusion;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTask;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTemplate;
import com.github.derrop.cloudnettransformer.util.StringUtils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CloudNet2Utils {

    public static Document templateToJson(CloudSystem cloudSystem, ServiceTask task, ServiceTemplate template, boolean allOptions) {
        return Documents.newDocument()
                .append("name", template.getName())
                .append("backend", "LOCAL")
                .append("url", (String) null)
                .append("processPreParameters", allOptions ? cloudSystem.getAllJvmOptions(task) : Collections.emptyList())
                .append("installablePlugins", allOptions ? cloudSystem.getAllInclusions(task).stream()
                        .map(inclusion -> Documents.newDocument()
                                .append("name", inclusion.getTarget())
                                .append("url", inclusion.getUrl())
                                .append("pluginResourceType", "URL")
                        )
                        .collect(Collectors.toList()) : Collections.emptyList()
                );
    }

    public static ServiceInclusion asInclusion(Document installablePlugin) {
        return new ServiceInclusion("plugins/" + StringUtils.randomString(16) + ".jar", installablePlugin.getString("url"));
    }

    public static Collection<ServiceInclusion> asInclusions(Document template) {
        return template.getDocuments("installablePlugins").stream()
                .filter(document -> document.getString("url") != null)
                .map(CloudNet2Utils::asInclusion)
                .collect(Collectors.toList());
    }

}
