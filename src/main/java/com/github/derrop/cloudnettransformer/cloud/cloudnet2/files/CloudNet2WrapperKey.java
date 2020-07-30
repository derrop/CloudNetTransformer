package com.github.derrop.cloudnettransformer.cloud.cloudnet2.files;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;
import com.github.derrop.cloudnettransformer.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@DescribedCloudExecutor(name = "WrapperKey", types = ExecutorType.WRITE)
public class CloudNet2WrapperKey implements CloudExecutor {

    private static final String WRAPPER_KEY_FILE = "WRAPPER_KEY.cnd";

    @Override
    public boolean execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {

        Path master = directory.resolve(Constants.MASTER_DIRECTORY);
        Path wrapper = directory.resolve(Constants.WRAPPER_DIRECTORY);

        Files.createDirectories(master);
        Files.createDirectories(wrapper);

        byte[] keyBytes = StringUtils.randomString(4096).getBytes();

        Files.write(master.resolve(WRAPPER_KEY_FILE), keyBytes, StandardOpenOption.CREATE);
        Files.write(wrapper.resolve(WRAPPER_KEY_FILE), keyBytes, StandardOpenOption.CREATE);

        return true;
    }
}
