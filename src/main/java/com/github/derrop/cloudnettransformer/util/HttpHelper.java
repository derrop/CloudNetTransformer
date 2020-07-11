package com.github.derrop.cloudnettransformer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpHelper {

    public static boolean download(String url, Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            return download(url, outputStream);
        }
    }

    public static boolean download(String url, OutputStream outputStream) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();

        if (connection.getResponseCode() != 200) {
            return false;
        }

        try (InputStream inputStream = connection.getInputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, read);
            }
        }

        connection.disconnect();

        return true;
    }

}
