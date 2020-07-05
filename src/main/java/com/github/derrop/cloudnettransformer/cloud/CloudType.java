package com.github.derrop.cloudnettransformer.cloud;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.reader.CloudReader;
import com.github.derrop.cloudnettransformer.cloud.reader.ReflectiveCloudReader;
import com.github.derrop.cloudnettransformer.cloud.writer.CloudWriter;
import com.github.derrop.cloudnettransformer.cloud.writer.ReflectiveCloudWriter;

import java.util.Arrays;

public enum CloudType {

    CLOUDNET_3("CloudNet 3", null, new ReflectiveCloudReader(prefixPackage("cloudnet3")), new ReflectiveCloudWriter(prefixPackage("cloudnet3"))),
    CLOUDNET_2(
            "CloudNet 2",
            "The directory has to contain '" + Constants.MASTER_DIRECTORY + "' and '" + Constants.WRAPPER_DIRECTORY + "' directories",
            new ReflectiveCloudReader(prefixPackage("cloudnet2")), new ReflectiveCloudWriter(prefixPackage("cloudnet2"))
    ),;

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
