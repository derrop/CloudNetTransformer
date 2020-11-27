package com.github.derrop.cloudnettransformer.cloud.executor.defaults;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorType;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "Multi")
public class MultiCloudExecutor implements CloudExecutor {

    private final List<ExecutorContainer> executors;

    public MultiCloudExecutor(Collection<CloudExecutor> executors) {
        this.executors = new ArrayList<>(executors.size());
        for (CloudExecutor executor : executors) {
            this.addExecutor(executor);
        }
    }

    @Override
    public String getOverriddenName() {
        return this.executors.stream().map(ExecutorContainer::getName).collect(Collectors.joining(", "));
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
    public ExecuteResult execute(ExecutorType type, CloudSystem cloudSystem, Path directory) throws IOException {
        this.executors.sort(Comparator.comparingInt(value -> value.getDescription().priority()));

        for (ExecutorContainer reader : this.executors) {
            if (!reader.hasType(type)) {
                continue;
            }

            System.out.println("Executing '" + reader.getName() + "' as " + type + "...");
            ExecuteResult result = reader.getExecutor().execute(type, cloudSystem, directory);
            if (!result.isSuccess()) {
                System.err.println(Ansi.ansi().fgBrightRed().a("Failed to execute '" + reader.getName() + "': " + result.getMessage()).reset());
                if (reader.getDescription().optional()) {
                    continue;
                }
                return result;
            }
            System.out.println(Ansi.ansi().fgBrightGreen().a("Successfully executed '" + reader.getName() + "'").reset());
        }
        return ExecuteResult.success();
    }
}
