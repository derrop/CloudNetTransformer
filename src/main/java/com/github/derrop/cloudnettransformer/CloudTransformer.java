package com.github.derrop.cloudnettransformer;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.CloudType;

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

    public void transform(Path sourceDirectory, Path targetDirectory, CloudType sourceType, CloudType targetType) throws IOException {
        System.out.println("Transforming " + sourceType.getName() + " (located at " + sourceDirectory.toAbsolutePath() + ") to " + targetType.getName() + " (located at " + targetDirectory.toAbsolutePath() + ")...");

        CloudSystem cloudSystem = new CloudSystem();
        sourceType.getReader().read(cloudSystem, sourceDirectory);
        targetType.getWriter().write(cloudSystem, targetDirectory);
    }

    public void askConsole() throws IOException {
        CloudType source;

        do {
            System.out.println("Which CloudSystem are you currently running on?");
            System.out.println("Possible answers: " + Arrays.stream(CloudType.values()).map(CloudType::getName).collect(Collectors.joining(", ")));

            source = CloudType.getByName(this.readLine());
        } while (source == null);

        CloudType finalSource = source;
        CloudType target;

        do {
            System.out.println("Which CloudSystem do you want to run on?");
            System.out.println("Possible answers: " + Arrays.stream(CloudType.values()).filter(cloudType -> cloudType != finalSource).map(CloudType::getName).collect(Collectors.joining(", ")));

            target = CloudType.getByName(this.readLine());
            if (target == source) {
                target = null;
            }
        } while (target == null);

        CloudType finalTarget = target;

        Path directory;

        do {
            System.out.println("Where is your Cloud located at?");
            if (source.getHint() != null) {
                System.out.println(source.getHint());
            }

            try {
                directory = Paths.get(this.readLine());

                if (!Files.exists(directory)) {
                    directory = null;
                }
            } catch (InvalidPathException exception) {
                System.out.println(exception.getMessage());
                directory = null;
            }
        } while (directory == null);

        Path targetDirectory = Paths.get(target.getName().replace(' ', '_'));
        Files.createDirectories(targetDirectory);

        this.transform(directory, targetDirectory, finalSource, finalTarget);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        CloudTransformer transformer = new CloudTransformer(reader);

        transformer.askConsole();
    }

}
