package com.github.derrop.cloudnettransformer;

import com.github.derrop.cloudnettransformer.cloud.CloudType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.UserNote;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

// TODO replace simplenametags/chat
public class CloudTransformer {

    private static final String PROMPT = Ansi.ansi()
            .fgBrightRed().a(System.getProperty("user.name"))
            .reset().fgBrightDefault().a(" > ")
            .toString();

    private final LineReader reader;

    public CloudTransformer(LineReader reader) {
        this.reader = reader;
    }

    public String readLine() {
        try {
            return this.reader.readLine(PROMPT);
        } catch (UserInterruptException exception) {
            System.exit(1);
            return null;
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
        DatabaseProvider sourceDatabaseProvider = cloudSystem.getDatabaseProvider();
        targetType.getExecutor().execute(ExecutorType.WRITE, cloudSystem, targetDirectory);
        DatabaseProvider targetDatabaseProvider = cloudSystem.getDatabaseProvider();

        if (sourceDatabaseProvider != null) {
            sourceDatabaseProvider.close();
        }
        if (targetDatabaseProvider != null) {
            targetDatabaseProvider.close();
        }

        String description = targetType.createDescription(sourceType);
        if (description != null && !description.trim().isEmpty()) {
            Collection<UserNote> notes = new ArrayList<>(cloudSystem.getNotes());
            cloudSystem.getNotes().clear();
            cloudSystem.addNote(UserNote.upgrade(description));
            cloudSystem.getNotes().addAll(notes);
        }

        if (!cloudSystem.getNotes().isEmpty()) {
            System.out.println();
            System.out.println(Ansi.ansi().fgBrightCyan().a("Notes (you can still read them later in the transformerNotes.txt in the output directory):").reset());
            for (UserNote note : cloudSystem.getNotes()) {
                System.out.println(note.formatAnsi());
            }

            Collection<String> notes = cloudSystem.getNotes().stream().map(UserNote::format).collect(Collectors.toList());
            Files.write(targetDirectory.resolve("transformerNotes.txt"), notes, StandardOpenOption.CREATE);
        }
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

                if (Files.notExists(directory) || !Files.isDirectory(directory)) {
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
        AnsiConsole.systemInstall();

        InputStream header = CloudTransformer.class.getClassLoader().getResourceAsStream("header.txt");
        if (header != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(header, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(Ansi.ansi().fgBright(Ansi.Color.BLUE).a(line).reset());
                }
                System.out.println();
            }
        }

        LineReader reader = LineReaderBuilder.builder().build();

        CloudTransformer transformer = new CloudTransformer(reader);

        transformer.askConsole();
    }

}
