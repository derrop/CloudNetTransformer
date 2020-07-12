package com.github.derrop.cloudnettransformer.util;

public interface ThrowableFunction<I, O, E extends Throwable> {

    O apply(I input) throws E;

}
