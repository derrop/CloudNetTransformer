package com.github.derrop.cloudnettransformer.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudConfig;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.JsonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@DescribedCloudExecutor(name = "Config")
public class CloudNet3Config implements CloudReaderWriter {

    private Path config(Path directory) {
        return directory.resolve("config.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        CloudConfig config = cloudSystem.getConfig();
        if (config == null) {
            return false;
        }

        Document document = Documents.newDocument();

        document.append("identity", Documents.newDocument()
                .append("uniqueId", config.getComponentName())
                .append("listeners", Collections.singletonList(Documents.newDocument().append("host", config.getIp()).append("port", config.getMainPort())))
                .append("properties", Documents.newDocument())
        );
        document.append("ipWhitelist", Arrays.asList("127.0.0.1", "127.0.1.1", config.getIp()));
        document.append("cluster", Documents.newDocument().append("clusterId", UUID.randomUUID()).append("nodes", Collections.emptyList()));

        document
                .append("maxCPUUsageToStartServices", config.getMaxCPUUsageToStartServices())
                .append("parallelServiceStartSequence", true)
                .append("runBlockedServiceStartTryLaterAutomatic", true)
                .append("maxMemory", config.getMaxMemory())
                .append("maxServiceConsoleLogCacheSize", 64)
                .append("printErrorStreamLinesFromServices", true)
                .append("jvmCommand", config.getJvmCommand())
                .append("hostAddress", config.getIp())
                .append("httpListeners", Collections.emptyList());

        Document sslConfig = Documents.newDocument()
                .append("enabled", false)
                .append("clientAuth", false)
                .append("trustCertificatePath", JsonNull.INSTANCE)
                .append("certificatePath", "local/certificate.pem")
                .append("privateKeyPath", "local/privateKey.key");

        for (String key : Arrays.asList("clientSslConfig", "serverSslConfig", "webSslConfig")) {
            document.append(key, sslConfig);
        }

        document.append("defaultJVMOptionParameters", true);

        Documents.jsonStorage().write(document, this.config(directory));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {
        Path path = this.config(directory);
        if (Files.notExists(path)) {
            return false;
        }

        Document document = Documents.jsonStorage().read(path);
        Document identity = document.getDocument("identity");
        if (identity == null) {
            return false;
        }
        Collection<Document> listeners = identity.getDocuments("listeners");
        Document listener = listeners.isEmpty() ? null : listeners.iterator().next();
        if (listener == null) {
            return false;
        }


        cloudSystem.setConfig(new CloudConfig(
                identity.getString("uniqueId"),
                false,
                listener.getString("host"),
                listener.getInt("port"),
                document.getDouble("maxCPUUsageToStartServices"),
                document.getString("jvmCommand"),
                document.getInt("maxMemory")
        ));

        return true;
    }
}
