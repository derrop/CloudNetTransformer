package com.github.derrop.cloudnettransformer.cloud.executor.defaults;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@DescribedCloudExecutor(name = "Multi")
public class MultiCloudExecutor implements CloudExecutor {

    private final List<ExecutorContainer> executors;

    public MultiCloudExecutor(Collection<CloudExecutor> executors) {
        this.executors = new ArrayList<>(executors.size());
        for (CloudExecutor executor : executors) {
            this.addExecutor(executor);
        }
    }

    @CanIgnoreReturnValue
    public MultiCloudExecutor addExecutor(CloudExecutor executor) {
        Class<?> currentClass = executor.getClass();
        DescribedCloudExecutor description;
        do {
            description = currentClass.getAnnotation(DescribedCloudExecutor.class);
            currentClass = currentClass.getSuperclass();
        } while (description == null && currentClass != null);

        if (description == null) {
            throw new IllegalArgumentException("Cannot use executor " + executor.getClass().getSimpleName() + " without the DescribedCloudExecutor annotation");
        }
        this.executors.add(new ExecutorContainer(executor, description));
        return this;
    }

    @Override
    public boolean execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {
        this.executors.sort(Comparator.comparingInt(value -> value.getDescription().priority()));

        for (ExecutorContainer reader : this.executors) {
            System.out.println("Executing '" + reader.getName() + "' as " + type + "...");
            if (!reader.getExecutor().execute(type, cloudSystem, directory)) {
                System.err.println("Failed to execute '" + reader.getName() + "'");
                return false;
            }
            System.out.println("Successfully executed '" + reader.getName() + "'");
        }
        return true;
    }
}
