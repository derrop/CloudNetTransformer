package com.github.derrop.cloudnettransformer.cloud.executor.defaults;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;
import com.github.derrop.cloudnettransformer.util.HttpHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@DescribedCloudExecutor(name = "", types = ExecutorType.WRITE)
public class FileDownloaderExecutor implements CloudExecutor {

    private final String name;
    private final String url;
    private final String path;

    public FileDownloaderExecutor(String name, String url, String path) {
        this.name = name;
        this.url = url;
        this.path = path;
    }

    public FileDownloaderExecutor(String url, String path) {
        this(null, url, path);
    }

    @Override
    public String getOverriddenName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getPath() {
        return this.path;
    }

    protected boolean downloadFile(CloudSystem cloudSystem, Path directory) throws IOException {
        String url = this.url;
        for (Map.Entry<String, String> entry : cloudSystem.getVariables().entrySet()) {
            url = url.replace(entry.getKey(), entry.getValue());
        }

        Path path = directory.resolve(this.path);
        Files.createDirectories(path.getParent());
        return HttpHelper.download(url, path);
    }

    @Override
    public ExecuteResult execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {
        if (type != ExecutorType.WRITE) {
            return ExecuteResult.success();
        }
        return ExecuteResult.of(this.downloadFile(cloudSystem, directory));
    }
}
