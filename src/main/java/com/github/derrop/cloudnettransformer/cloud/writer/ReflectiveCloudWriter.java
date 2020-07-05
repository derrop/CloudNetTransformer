package com.github.derrop.cloudnettransformer.cloud.writer;

import com.github.derrop.cloudnettransformer.util.ClassFinder;

public class ReflectiveCloudWriter extends MultiCloudWriter {

    public ReflectiveCloudWriter(String packagePrefix) {
        super(ClassFinder.of(packagePrefix, CloudWriter.class).discover());
    }

}
