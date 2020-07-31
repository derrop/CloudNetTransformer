package com.github.derrop.cloudnettransformer.cloud.executor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DescribedCloudExecutor {

    String name();

    ExecutorType[] types() default {ExecutorType.READ, ExecutorType.WRITE};

    int priority() default ExecutorPriority.NORMAL;

    boolean optional() default true;

}
