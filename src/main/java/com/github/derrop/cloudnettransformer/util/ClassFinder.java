package com.github.derrop.cloudnettransformer.util;

import com.google.common.reflect.ClassPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class ClassFinder<T> {

    private final String prefix;
    private final Class<?> superClass;

    private ClassFinder(@NotNull String prefix, @Nullable Class<T> superClass) {
        this.prefix = prefix;
        this.superClass = superClass;
    }

    public static <T> ClassFinder<T> of(@NotNull String prefix, @Nullable Class<T> superClass) {
        return new ClassFinder<>(prefix, superClass);
    }

    public static ClassFinder<?> of(@NotNull String prefix) {
        return of(prefix, null);
    }

    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    public Collection<T> discover() {
        Collection<T> out = new ArrayList<>();

        try {
            for (ClassPath.ClassInfo classInfo : ClassPath.from(super.getClass().getClassLoader()).getTopLevelClassesRecursive(this.prefix)) {
                Class<?> discovered = classInfo.load();
                if (this.superClass == null || this.superClass.isAssignableFrom(discovered)) {
                    T t = (T) discovered.getDeclaredConstructor().newInstance();
                    out.add(t);
                }
            }
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        return out;
    }

}
