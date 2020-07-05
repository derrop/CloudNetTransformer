package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

public enum SignMessage {

    SERVER_CONNECTING("&7You will be moved to &c%server%&7..."),
    SIGN_REMOVE_SUCCESS("&7The target sign will removed! Please wait..."),
    SIGN_CREATE_SUCCESS("&7The target sign with the target group &6%group% &7is successfully created."),
    SIGN_CLEANUP_SUCCESS("&7Non-existing signs were removed successfully"),
    SIGN_ALREADY_EXISTS("&7The sign is already set. If you want to remove that, use the /cloudsign remove command");

    private final String defaultMessage;

    SignMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }
}
