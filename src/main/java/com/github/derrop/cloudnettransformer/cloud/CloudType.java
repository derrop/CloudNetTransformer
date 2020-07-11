package com.github.derrop.cloudnettransformer.cloud;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.reader.CloudReader;
import com.github.derrop.cloudnettransformer.cloud.reader.ReflectiveCloudReader;
import com.github.derrop.cloudnettransformer.cloud.writer.CloudWriter;
import com.github.derrop.cloudnettransformer.cloud.writer.FileDownloader;
import com.github.derrop.cloudnettransformer.cloud.writer.ReflectiveCloudWriter;
import com.github.derrop.cloudnettransformer.cloud.writer.StartFileWriter;

import java.util.Arrays;

public enum CloudType {

    CLOUDNET_3(
            "CloudNet 3",
            null,
            new ReflectiveCloudReader(prefixPackage("cloudnet3")),
            new ReflectiveCloudWriter(prefixPackage("cloudnet3"))
                    .addWriter(new FileDownloader("CloudNet-Node", "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet-v3/job/master/lastSuccessfulBuild/artifact/cloudnet-launcher/build/libs/launcher.jar", "launcher.jar"))
                    .addWriter(new StartFileWriter("CloudNet-Node", "https://raw.githubusercontent.com/CloudNetService/CloudNet-v3/master/.template/start", "start"))
    ),
    CLOUDNET_2(
            "CloudNet 2",
            "The directory has to contain '" + Constants.MASTER_DIRECTORY + "' and '" + Constants.WRAPPER_DIRECTORY + "' directories",
            new ReflectiveCloudReader(prefixPackage("cloudnet2")),
            new ReflectiveCloudWriter(prefixPackage("cloudnet2"))
                    .addWriter(new FileDownloader("CloudNet-Master", "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet/job/master/lastSuccessfulBuild/artifact/cloudnet-core/target/CloudNet-Master.jar", Constants.MASTER_DIRECTORY + "/CloudNet-Master.jar"))
                    .addWriter(new FileDownloader("CloudNet-Wrapper", "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet/job/master/lastSuccessfulBuild/artifact/cloudnet-wrapper/target/CloudNet-Wrapper.jar", Constants.WRAPPER_DIRECTORY + "/CloudNet-Wrapper.jar"))
                    .addWriter(new StartFileWriter("CloudNet-Master", "https://raw.githubusercontent.com/CloudNetService/CloudNet/master/.template/CloudNet-Master/start", Constants.MASTER_DIRECTORY + "/start"))
                    .addWriter(new StartFileWriter("CloudNet-Wrapper", "https://raw.githubusercontent.com/CloudNetService/CloudNet/master/.template/CloudNet-Wrapper/start", Constants.WRAPPER_DIRECTORY + "/start"))
    );

    private final String name;
    private final String hint;
    private final CloudReader reader;
    private final CloudWriter writer;

    CloudType(String name, String hint, CloudReader reader, CloudWriter writer) {
        this.name = name;
        this.hint = hint;
        this.reader = reader;
        this.writer = writer;
    }

    public String getName() {
        return this.name;
    }

    public String getHint() {
        return this.hint;
    }

    public CloudReader getReader() {
        return this.reader;
    }

    public CloudWriter getWriter() {
        return this.writer;
    }

    private static String prefixPackage(String suffix) {
        return CloudType.class.getPackage().getName() + "." + suffix;
    }

    public static CloudType getByName(String name) {
        return Arrays.stream(values()).filter(cloudType -> cloudType.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
