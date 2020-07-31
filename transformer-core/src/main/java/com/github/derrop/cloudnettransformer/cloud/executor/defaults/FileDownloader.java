package com.github.derrop.cloudnettransformer.cloud.executor.defaults;

import com.github.derrop.cloudnettransformer.util.HttpHelper;

import java.io.IOException;
import java.nio.file.Path;

public class FileDownloader {

    private final String name;
    private final String url;
    private final String path;

    public FileDownloader(String name, String url, String path) {
        this.name = name;
        this.url = url;
        this.path = path;
    }

    public FileDownloader(String url, String path) {
        this(null, url, path);
    }

    public String getOverriddenName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getPath() {
        return this.path;
    }

    protected boolean downloadFile(Path directory) throws IOException {
        return HttpHelper.download(this.url, directory.resolve(this.path));
    }

}
