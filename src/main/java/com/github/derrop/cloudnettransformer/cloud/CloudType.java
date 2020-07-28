package com.github.derrop.cloudnettransformer.cloud;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.ReflectiveCloudExecutor;

import java.util.Arrays;

public enum CloudType {

    CLOUDNET_3(
            "CloudNet 3",
            null,
            new ReflectiveCloudExecutor(prefixPackage("cloudnet3"))
    ),
    CLOUDNET_2(
            "CloudNet 2",
            "The directory has to contain '" + Constants.MASTER_DIRECTORY + "' and '" + Constants.WRAPPER_DIRECTORY + "' directories",
            new ReflectiveCloudExecutor(prefixPackage("cloudnet2"))
    );

    private final String name;
    private final String hint;
    private final CloudExecutor executor;

    CloudType(String name, String hint, CloudExecutor executor) {
        this.name = name;
        this.hint = hint;
        this.executor = executor;
    }

    public String getName() {
        return this.name;
    }

    public String getHint() {
        return this.hint;
    }

    public CloudExecutor getExecutor() {
        return this.executor;
    }

    private static String prefixPackage(String suffix) {
        return CloudType.class.getPackage().getName() + "." + suffix;
    }

    public static CloudType getByName(String name) {
        return Arrays.stream(values()).filter(cloudType -> cloudType.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
