package com.github.derrop.cloudnettransformer.cloud.executor.defaults;

import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.util.ClassFinder;

public class ReflectiveCloudExecutor extends MultiCloudExecutor {

    public ReflectiveCloudExecutor(String packagePrefix) {
        super(ClassFinder.of(packagePrefix, CloudExecutor.class).discover());
    }

}
