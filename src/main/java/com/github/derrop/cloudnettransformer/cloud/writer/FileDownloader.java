package com.github.derrop.cloudnettransformer.cloud.writer;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.util.HttpHelper;

import java.io.IOException;
import java.nio.file.Path;

public class FileDownloader implements CloudWriter {

    private final String name;
    private final String url;
    private final String path;

    public FileDownloader(String name, String url, String path) {
        this.name = name;
        this.url = url;
        this.path = path;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        return HttpHelper.download(this.url, directory.resolve(this.path));
    }
}
