package com.github.derrop.cloudnettransformer.cloud;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.VariableLoader;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.defaults.ReflectiveCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.variable.CloudNet3VariableLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

public enum CloudType {

    CLOUDNET_3(
            "CloudNet 3",
            null,
            new ReflectiveCloudExecutor(prefixPackage("cloudnet3")),
            new CloudNet3VariableLoader()),
    CLOUDNET_2(
            "CloudNet 2",
            "The directory has to contain '" + Constants.MASTER_DIRECTORY + "' and '" + Constants.WRAPPER_DIRECTORY + "' directories",
            new ReflectiveCloudExecutor(prefixPackage("cloudnet2")),
            null);

    private final String name;
    private final String hint;
    private final CloudExecutor executor;
    private final VariableLoader variableLoader;

    private Function<CloudType, String> descriptionProvider;

    CloudType(String name, String hint, CloudExecutor executor, VariableLoader variableLoader) {
        this(name, hint, executor, variableLoader, null);
        this.descriptionProvider = descriptionProvider(this);
    }

    CloudType(String name, String hint, CloudExecutor executor, VariableLoader variableLoader, Function<CloudType, String> descriptionProvider) {
        this.name = name;
        this.hint = hint;
        this.executor = executor;
        this.variableLoader = variableLoader;
        this.descriptionProvider = descriptionProvider;
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

    public VariableLoader getVariableLoader() {
        return this.variableLoader;
    }

    private static Function<CloudType, String> descriptionProvider(CloudType target) {
        return source -> readDescription(source, target);
    }

    private static String readDescription(CloudType source, CloudType target) {
        String path = String.format("descriptions/%s/%s.txt", source.name().toLowerCase(), target.name().toLowerCase());
        try (InputStream inputStream = CloudType.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                return null;
            }

            StringBuilder output = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("#")) {
                        output.append(line).append('\n');
                    }
                }
            }

            return output.toString();
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public String createDescription(CloudType source) {
        return this.descriptionProvider == null ? null : this.descriptionProvider.apply(source);
    }

    private static String prefixPackage(String suffix) {
        return "com.github.derrop.cloudnettransformer." + suffix;
    }

    public static CloudType getByName(String name) {
        return Arrays.stream(values()).filter(cloudType -> cloudType.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
