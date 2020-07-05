package com.github.derrop.cloudnettransformer.cloud.reader;

import com.github.derrop.cloudnettransformer.util.ClassFinder;

public class ReflectiveCloudReader extends MultiCloudReader {

    public ReflectiveCloudReader(String packagePrefix) {
        super(ClassFinder.of(packagePrefix, CloudReader.class).discover());
    }

}
