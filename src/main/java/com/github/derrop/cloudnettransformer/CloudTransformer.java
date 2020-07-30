package com.github.derrop.cloudnettransformer;

import com.github.derrop.cloudnettransformer.cloud.CloudType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CloudTransformer {

    private final BufferedReader reader; // TODO: replace with JLine

    public CloudTransformer(BufferedReader reader) {
        this.reader = reader;
    }

    public String readLine() {
        try {
            return this.reader.readLine();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read line from console", exception);
        }
    }

    public void transform(Path sourceDirectory, CloudType sourceType, CloudType targetType) throws IOException {
        Path targetDirectory = Paths.get(targetType.getName().replace(' ', '_'));
        Files.createDirectories(targetDirectory);

        this.transform(sourceDirectory, targetDirectory, sourceType, targetType);
    }

    public void transform(Path sourceDirectory, Path targetDirectory, CloudType sourceType, CloudType targetType) throws IOException {
        System.out.println("Transforming " + sourceType.getName() + " (located at " + sourceDirectory.toAbsolutePath() + ") to " + targetType.getName() + " (located at " + targetDirectory.toAbsolutePath() + ")...");

        CloudSystem cloudSystem = new CloudSystem();
        sourceType.getExecutor().execute(ExecutorType.READ, cloudSystem, sourceDirectory);
        targetType.getExecutor().execute(ExecutorType.WRITE, cloudSystem, sourceDirectory);
    }

    public void askConsole() throws IOException {
        CloudType source = this.readSource();
        CloudType target = this.readTarget(source);
        Path directory = this.readSourceDirectory(source);

        this.transform(directory, source, target);
    }

    private CloudType readSource() {
        CloudType source;
        do {
            System.out.println("Which CloudSystem are you currently running on?");
            System.out.println("Possible answers: " + Arrays.stream(CloudType.values()).map(CloudType::getName).collect(Collectors.joining(", ")));

            source = CloudType.getByName(this.readLine());
        } while (source == null);
        return source;
    }

    private CloudType readTarget(CloudType source) {
        CloudType[] remainingTypes = Arrays.stream(CloudType.values()).filter(cloudType -> cloudType != source).toArray(CloudType[]::new);
        if (remainingTypes.length == 1) {
            return remainingTypes[0];
        }

        CloudType target;

        do {
            System.out.println("Which CloudSystem do you want to run on?");
            System.out.println("Possible answers: " + Arrays.stream(remainingTypes).map(CloudType::getName).collect(Collectors.joining(", ")));

            target = CloudType.getByName(this.readLine());
            if (target == source) {
                target = null;
            }
        } while (target == null);

        return target;
    }

    private Path readSourceDirectory(CloudType source) {
        Path directory;

        do {
            System.out.println("Where is your " + source.getName() + " located at?");
            if (source.getHint() != null) {
                System.out.println(source.getHint());
            }

            try {
                directory = Paths.get(this.readLine());

                if (Files.notExists(directory)) {
                    directory = null;
                }
            } catch (InvalidPathException exception) {
                System.out.println(exception.getMessage());
                directory = null;
            }
        } while (directory == null);

        return directory;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        CloudTransformer transformer = new CloudTransformer(reader);

        transformer.askConsole();
    }

}
