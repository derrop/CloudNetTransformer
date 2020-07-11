package com.github.derrop.cloudnettransformer.cloud.writer;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.util.HttpHelper;

import java.io.IOException;
import java.nio.file.Path;

public class StartFileWriter extends FileDownloader {

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

    public StartFileWriter(String name, String url, String path) {
        super(name, url, path);
    }

    @Override
    public String getName() {
        return super.getName() + " StartFile";
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        if (getFileSuffix() == null) {
            System.out.println("Not writing start file because the OperatingSystem '" + System.getProperty("os.name") + "' could not be detected");
            return true;
        }

        return HttpHelper.download(super.getUrl() + getFileSuffix(), directory.resolve(super.getPath() + getFileSuffix()));
    }
}
