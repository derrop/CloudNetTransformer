package com.github.derrop.cloudnettransformer.cloud.executor;

public class ExecuteResult {

    private static final ExecuteResult SUCCESS = new ExecuteResult(true, null);

    private final boolean success;
    private final String message;

    private ExecuteResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ExecuteResult of(boolean success) {
        return success ? success() : failed("Unknown");
    }

    public static ExecuteResult success() {
        return SUCCESS;
    }

    public static ExecuteResult failed(String message) {
        return new ExecuteResult(false, message);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }
}
