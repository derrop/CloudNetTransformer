package com.github.derrop.cloudnettransformer.cloud.cloudnet2;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.writer.FileDownloader;
import com.github.derrop.cloudnettransformer.cloud.writer.MultiCloudWriter;
import com.github.derrop.cloudnettransformer.cloud.writer.StartFileWriter;

import java.util.Arrays;

public class CloudNet2Files extends MultiCloudWriter {
    public CloudNet2Files() {
        super(Arrays.asList(
                new FileDownloader("CloudNet-Master", "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet/job/master/lastSuccessfulBuild/artifact/cloudnet-core/target/CloudNet-Master.jar", Constants.MASTER_DIRECTORY + "/CloudNet-Master.jar"),
                new FileDownloader("CloudNet-Wrapper", "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet/job/master/lastSuccessfulBuild/artifact/cloudnet-wrapper/target/CloudNet-Wrapper.jar", Constants.WRAPPER_DIRECTORY + "/CloudNet-Wrapper.jar"),
                new StartFileWriter("CloudNet-Master", "https://raw.githubusercontent.com/CloudNetService/CloudNet/master/.template/CloudNet-Master/start", Constants.MASTER_DIRECTORY + "/start"),
                new StartFileWriter("CloudNet-Wrapper", "https://raw.githubusercontent.com/CloudNetService/CloudNet/master/.template/CloudNet-Wrapper/start", Constants.WRAPPER_DIRECTORY + "/start")
        ));
    }
}
