package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.writer.FileDownloader;
import com.github.derrop.cloudnettransformer.cloud.writer.MultiCloudWriter;
import com.github.derrop.cloudnettransformer.cloud.writer.StartFileWriter;

import java.util.Arrays;

public class CloudNet3Files extends MultiCloudWriter {
    public CloudNet3Files() {
        super(Arrays.asList(
                new FileDownloader("CloudNet-Node", "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet-v3/job/master/lastSuccessfulBuild/artifact/cloudnet-launcher/build/libs/launcher.jar", "launcher.jar"),
                new StartFileWriter("CloudNet-Node", "https://raw.githubusercontent.com/CloudNetService/CloudNet-v3/master/.template/start", "start")
        ));
    }
}
