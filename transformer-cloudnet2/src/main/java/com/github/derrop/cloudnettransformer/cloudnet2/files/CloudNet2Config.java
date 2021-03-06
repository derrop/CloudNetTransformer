package com.github.derrop.cloudnettransformer.cloudnet2.files;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudConfig;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@DescribedCloudExecutor(name = "Config")
public class CloudNet2Config implements CloudReaderWriter {

    private Path masterConfig(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("config.yml");
    }

    private Path wrapperConfig(Path directory) {
        return directory.resolve(Constants.WRAPPER_DIRECTORY).resolve("config.yml");
    }

    @Override
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) {
        CloudConfig config = cloudSystem.getConfig();
        if (config == null) {
            return ExecuteResult.failed("No Config in the CloudSystem set");
        }

        Document master = Documents.newDocument();
        Document wrapper = Documents.newDocument();

        master.append("general", Documents.newDocument()
                .append("auto-update", false)
                .append("server-name-splitter", "-")
                .append("dynamicservices", false)
                .append("notify-service", config.shouldNotifyServerUpdates())
                .append("disabled-modules", cloudSystem.getPermissionConfiguration().isEnabled() ? Collections.emptyList() : Collections.singletonList("CloudNet-Service-PermissionModule"))
                .append("cloudGameServer-wrapperList", Collections.emptyList())
                .append("haste", Documents.newDocument().append("server", Arrays.asList("https://hastebin.com", "https://hasteb.in", "https://just-paste.it")))
        );
        master.append("server", Documents.newDocument()
                .append("hostaddress", config.getIp())
                .append("ports", Collections.singletonList(config.getMainPort()))
                .append("webservice", Documents.newDocument().append("hostaddress", config.getIp()).append("port", 1420))
        );
        master.append("cloudnet-statistics", Documents.newDocument().append("enabled", true).append("uuid", UUID.randomUUID()));
        master.append("networkproperties", Documents.newDocument().append("test", true));

        wrapper.append("connection", Documents.newDocument()
                .append("cloudnet-host", config.getIp())
                .append("cloudnet-port", config.getMainPort())
                .append("cloudnet-web", 1420)
        );
        wrapper.append("general", Documents.newDocument()
                .append("wrapperId", config.getComponentName())
                .append("internalIp", config.getIp())
                .append("proxy-config-host", config.getIp())
                .append("max-memory", config.getMaxMemory())
                .append("startPort", 41570)
                .append("auto-update", false)
                .append("saving-records", false)
                .append("viaversion", false)
                .append("maintenance-copyFileToDirectory", false)
                .append("devservicePath", directory.resolve(Constants.WRAPPER_DIRECTORY).resolve("Development").toAbsolutePath().toString())
                .append("processQueueSize", 4)
                .append("percentOfCPUForANewServer", config.getMaxCPUUsageToStartServices())
                .append("percentOfCPUForANewCloudServer", config.getMaxCPUUsageToStartServices())
                .append("percentOfCPUForANewProxy", config.getMaxCPUUsageToStartServices())
        );

        master.yaml().write(this.masterConfig(directory));
        ;
        wrapper.yaml().write(this.wrapperConfig(directory));
        ;

        return ExecuteResult.success();
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) {
        Path masterConfigPath = this.masterConfig(directory);
        Path wrapperConfigPath = this.wrapperConfig(directory);
        if (Files.notExists(masterConfigPath) || Files.notExists(wrapperConfigPath)) {
            return ExecuteResult.failed("No Master/Wrapper configs found in " + masterConfigPath + " and " + wrapperConfigPath);
        }

        Document master = Documents.yamlStorage().read(masterConfigPath);
        Document wrapper = Documents.yamlStorage().read(wrapperConfigPath);

        Document masterGeneral = master.getDocument("general");
        Document masterServer = master.getDocument("server");
        Document wrapperGeneral = wrapper.getDocument("general");
        if (masterGeneral == null || masterServer == null || wrapperGeneral == null) {
            return ExecuteResult.failed("No general or server entry in the Master config or no general entry in the Wrapper config found");
        }

        cloudSystem.setConfig(new CloudConfig(
                wrapperGeneral.getString("wrapperId"),
                masterGeneral.getBoolean("notify-service"),
                masterServer.getString("hostaddress"),
                masterServer.get("ports", int[].class)[0],
                (wrapperGeneral.getDouble("percentOfCPUForANewServer") + wrapperGeneral.getDouble("percentOfCPUForANewCloudServer") + wrapperGeneral.getDouble("percentOfCPUForANewProxy")) / 3D,
                "java",
                wrapperGeneral.getInt("max-memory")
        ));

        return ExecuteResult.success();
    }
}
