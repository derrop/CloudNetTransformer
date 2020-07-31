package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceEnvironment;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceInclusion;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class CloudNet3Utils {

    private static final Map<ServiceEnvironment, String[]> APPLICATION_FILE_CONTENTS = ImmutableMap.of(
            ServiceEnvironment.MINECRAFT_SERVER, new String[]{"spigot", "paper", "glowstone"},
            ServiceEnvironment.BUNGEECORD, new String[]{"bungee", "waterfall", "travertine", "hexacord"}
    );
    private static final String[] SPIGOT_EXTRA_FILES = new String[]{"server.properties", "spigot.yml"};

    public static Document inclusionToDocument(ServiceInclusion inclusion) {
        Document document = Documents.newDocument().append("destination", inclusion.getTarget()).append("url", inclusion.getUrl());
        if (!inclusion.getHeaders().isEmpty()) {
            document.append("properties", Documents.newDocument("httpHeaders", inclusion.getHeaders()));
        }
        return document;
    }

    public static ServiceInclusion documentToInclusion(Document document) {
        ServiceInclusion inclusion = new ServiceInclusion(document.getString("destination"), document.getString("url"));
        Document properties = document.getDocument("properties");
        if (properties != null) {
            Document headers = properties.getDocument("httpHeaders");
            if (headers != null) {
                for (String key : headers.keys()) {
                    String value = headers.getString(key);
                    if (value != null) {
                        inclusion.getHeaders().put(key, value);
                    }
                }
            }
        }
        return inclusion;
    }

    public static Path resolveApplicationPath(ServiceEnvironment environment, Path directory) throws IOException {
        String[] contents = APPLICATION_FILE_CONTENTS.get(environment);
        if (contents == null) {
            return null;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                if (!fileName.endsWith(".jar")) {
                    continue;
                }
                fileName = fileName.substring(0, fileName.length() - 4).toLowerCase();

                for (String content : contents) {
                    if (fileName.contains(content)) {
                        return path;
                    }
                }
            }
        }

        return null;
    }

    public static void copyExtraSpigotFiles(Path targetDirectory) throws IOException {
        for (String file : SPIGOT_EXTRA_FILES) {
            try (InputStream inputStream = CloudNet3Utils.class.getClassLoader().getResourceAsStream(file)) {
                if (inputStream != null) {
                    Files.copy(inputStream, targetDirectory.resolve(file), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

}
