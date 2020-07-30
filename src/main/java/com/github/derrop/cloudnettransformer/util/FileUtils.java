package com.github.derrop.cloudnettransformer.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public class FileUtils {

    public static void deleteDirectory(Path path) throws IOException {
        if (Files.notExists(path)) {
            return;
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void deleteFiles(Path directory, String... allowedFileNames) throws IOException {
        if (Files.notExists(directory)) {
            return;
        }

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Arrays.asList(allowedFileNames).contains(file.getFileName().toString())) {
                    Files.delete(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void copyDirectory(Path sourceDirectory, Path targetDirectory, String... excludedFileNames) throws IOException {
        if (!Files.exists(sourceDirectory)) {
            return;
        }

        Files.createDirectories(targetDirectory);

        Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(targetDirectory.resolve(sourceDirectory.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Arrays.asList(excludedFileNames).contains(file.getFileName().toString())) {
                    return FileVisitResult.CONTINUE;
                }
                Files.copy(file, targetDirectory.resolve(sourceDirectory.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
