package com.github.derrop.cloudnettransformer.cloud.deserialized.message;

public enum MessageType {

    GLOBAL_PREFIX(MessageCategory.GENERAL, "&7Cloud &8| &b"),

    COMMAND_HUB_CONNECT_SUCCESS(MessageCategory.GENERAL_COMMANDS, "&7You did successfully connect to %server%"),
    COMMAND_HUB_ALREADY_HUB(MessageCategory.GENERAL_COMMANDS, "&cYou are already connected"),
    COMMAND_HUB_NO_SERVER(MessageCategory.GENERAL_COMMANDS, "&7Hub server cannot be found"),
    COMMAND_CLOUD_NO_PERMISSION(MessageCategory.GENERAL_COMMANDS, "&7You are not allowed to use &b%command%"),
    ONLY_PROXY_JOIN_KICK(MessageCategory.ONLY_PROXY_JOIN, "&7You must connect from an internal proxy server"),

    LOGIN_NETWORK_FULL(MessageCategory.NETWORK, "&cThe network is currently full. You need extra permissions to enter the network"),
    NETWORK_ALREADY_CONNECTED(MessageCategory.NETWORK, "&cYou are already connected with this network"),
    SERVER_JOIN_MAINTENANCE(MessageCategory.NETWORK, "&7This server is currently in maintenance mode"),
    PROXY_JOIN_MAINTENANCE(MessageCategory.NETWORK, "&cThe network is currently in maintenance!"),
    SERVICE_STARTING(MessageCategory.INGAME_INFO, "&7The service &e%service% &7is &astarting..."),
    SERVICE_STOPPING(MessageCategory.INGAME_INFO, "&7The service &e%service% &7is &cstopping..."),

    SIGN_SERVER_CONNECTING(MessageCategory.SIGNS, "&7You will be moved to &c%server%&7..."),
    SIGN_REMOVE_SUCCESS(MessageCategory.SIGNS, "&7The target sign will removed! Please wait..."),
    SIGN_CREATE_SUCCESS(MessageCategory.SIGNS, "&7The target sign with the target group &6%group% &7is successfully created."),
    SIGN_CLEANUP_SUCCESS(MessageCategory.SIGNS, "&7Non-existing signs were removed successfully"),
    SIGN_ALREADY_EXISTS(MessageCategory.SIGNS, "&7The sign is already set. If you want to remove that, use the /cloudsign remove command");

    private final MessageCategory category;
    private final String defaultMessage;

    MessageType(MessageCategory category, String defaultMessage) {
        this.category = category;
        this.defaultMessage = defaultMessage;
    }

    public MessageCategory getCategory() {
        return this.category;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }

}
