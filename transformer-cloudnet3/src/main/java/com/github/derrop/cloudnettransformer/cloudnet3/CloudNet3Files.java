package com.github.derrop.cloudnettransformer.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.FileDownloaderExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.MultiCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.StartFileWriter;

import java.util.Arrays;

@DescribedCloudExecutor(name = "DefaultFiles")
public class CloudNet3Files extends MultiCloudExecutor {
    public CloudNet3Files() {
        super(Arrays.asList(
                new FileDownloaderExecutor("CloudNet-Node", "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet-v3/job/master/lastSuccessfulBuild/artifact/cloudnet-launcher/build/libs/launcher.jar", "launcher.jar"),
                new StartFileWriter("CloudNet-Node", "https://raw.githubusercontent.com/CloudNetService/CloudNet-v3/master/.template/start", "start")
        ));
    }
}
