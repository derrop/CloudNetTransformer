package com.github.derrop.cloudnettransformer.cloud.executor.defaults;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;
import com.github.derrop.cloudnettransformer.util.HttpHelper;

import java.io.IOException;
import java.nio.file.Path;

@DescribedCloudExecutor(name = "StartFileWriter", types = ExecutorType.WRITE)
public class StartFileWriter implements CloudExecutor {

    private static String fileSuffix;

    private static String getFileSuffix() {
        if (fileSuffix == null) {
            String os = System.getProperty("os.name", "generic").toLowerCase();
            if (os.contains("mac") || os.contains("darwin")) {
                fileSuffix = ".command";
            } else if (os.contains("win")) {
                fileSuffix = ".bat";
            } else if (os.contains("nux")) {
                fileSuffix = ".sh";
            } else {
                fileSuffix = null;
            }
        }
        return fileSuffix;
    }

    private final String name;
    private final String url;
    private final String path;

    public StartFileWriter(String name, String url, String path) {
        this.name = name;
        this.url = url;
        this.path = path;
    }

    public StartFileWriter(String url, String path) {
        this(null, url, path);
    }

    @Override
    public String getOverriddenName() {
        return this.name == null ? null : this.name + " StartFile";
    }

    @Override
    public boolean execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {
        if (getFileSuffix() == null) {
            System.out.println("Not writing start file because the OperatingSystem '" + System.getProperty("os.name") + "' could not be detected");
            return true;
        }

        return HttpHelper.download(this.url + getFileSuffix(), directory.resolve(this.path + getFileSuffix()));
    }
}
