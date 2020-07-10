package com.github.derrop.cloudnettransformer.cloud.cloudnet2;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.writer.CloudWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class CloudNet2WrapperKey implements CloudWriter {

    private static final String WRAPPER_KEY_FILE = "WRAPPER_KEY.cnd";

    @Override
    public String getName() {
        return "WrapperKey";
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {

        Path master = directory.resolve(Constants.MASTER_DIRECTORY);
        Path wrapper = directory.resolve(Constants.WRAPPER_DIRECTORY);

        Files.createDirectories(master);
        Files.createDirectories(wrapper);

        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            key.append(UUID.randomUUID().toString().replace("-", ""));
        }
        byte[] keyBytes = key.toString().getBytes();

        Files.write(master.resolve(WRAPPER_KEY_FILE), keyBytes, StandardOpenOption.CREATE);
        Files.write(wrapper.resolve(WRAPPER_KEY_FILE), keyBytes, StandardOpenOption.CREATE);

        return true;
    }
}
