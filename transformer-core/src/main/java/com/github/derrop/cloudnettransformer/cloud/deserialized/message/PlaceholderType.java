package com.github.derrop.cloudnettransformer.cloud.deserialized.message;

public enum PlaceholderType {

    TAB_PROXY(PlaceholderCategory.TAB_LIST),
    TAB_PROXY_ID(PlaceholderCategory.TAB_LIST),
    TAB_SERVER(PlaceholderCategory.TAB_LIST),
    TAB_SERVER_TASK(PlaceholderCategory.TAB_LIST),
    TAB_ONLINE_PLAYERS(PlaceholderCategory.TAB_LIST),
    TAB_MAX_PLAYERS(PlaceholderCategory.TAB_LIST),
    TAB_PROXY_TASK(PlaceholderCategory.TAB_LIST),
    TAB_PLAYER_NAME(PlaceholderCategory.TAB_LIST),
    TAB_PLAYER_PING(PlaceholderCategory.TAB_LIST),
    TAB_TIME(PlaceholderCategory.TAB_LIST),
    TAB_PERMISSION_GROUP_NAME(PlaceholderCategory.TAB_LIST),
    TAB_PERMISSION_GROUP_PREFIX(PlaceholderCategory.TAB_LIST),
    TAB_PERMISSION_GROUP_SUFFIX(PlaceholderCategory.TAB_LIST),
    TAB_PERMISSION_GROUP_DISPLAY(PlaceholderCategory.TAB_LIST),
    TAB_PERMISSION_GROUP_COLOR(PlaceholderCategory.TAB_LIST),

    MOTD_PROXY(PlaceholderCategory.MOTD),
    MOTD_PROXY_ID(PlaceholderCategory.MOTD),
    MOTD_TASK(PlaceholderCategory.MOTD),
    MOTD_NODE(PlaceholderCategory.MOTD),
    MOTD_ONLINE_PLAYERS(PlaceholderCategory.MOTD),
    MOTD_MAX_PLAYERS(PlaceholderCategory.MOTD),
    MOTD_VERSION(PlaceholderCategory.MOTD),

    SIGNS_TASK(PlaceholderCategory.SIGNS),
    SIGNS_TASK_ID(PlaceholderCategory.SIGNS),
    SIGNS_TARGET_GROUP(PlaceholderCategory.SIGNS),
    SIGNS_NAME(PlaceholderCategory.SIGNS),
    SIGNS_UUID(PlaceholderCategory.SIGNS),
    SIGNS_NODE(PlaceholderCategory.SIGNS),
    SIGNS_ENVIRONMENT(PlaceholderCategory.SIGNS),
    SIGNS_LIFE_CYCLE(PlaceholderCategory.SIGNS),
    SIGNS_RUNTIME(PlaceholderCategory.SIGNS),
    SIGNS_HOST(PlaceholderCategory.SIGNS),
    SIGNS_PORT(PlaceholderCategory.SIGNS),
    SIGNS_CPU_USAGE(PlaceholderCategory.SIGNS),
    SIGNS_MEMORY(PlaceholderCategory.SIGNS),
    SIGNS_THREADS(PlaceholderCategory.SIGNS),
    SIGNS_ONLINE(PlaceholderCategory.SIGNS),
    SIGNS_ONLINE_PLAYERS(PlaceholderCategory.SIGNS),
    SIGNS_MAX_PLAYERS(PlaceholderCategory.SIGNS),
    SIGNS_MOTD(PlaceholderCategory.SIGNS),
    SIGNS_EXTRA(PlaceholderCategory.SIGNS),
    SIGNS_STATE(PlaceholderCategory.SIGNS),
    SIGNS_TEMPLATE(PlaceholderCategory.SIGNS),
    SIGNS_VERSION(PlaceholderCategory.SIGNS),
    SIGNS_WHITELIST(PlaceholderCategory.SIGNS),
    SIGNS_PLACED_GROUP(PlaceholderCategory.SIGNS),

    NPCS_TASK(PlaceholderCategory.NPC_INVENTORY),
    NPCS_TASK_ID(PlaceholderCategory.NPC_INVENTORY),
    NPCS_TARGET_GROUP(PlaceholderCategory.NPC_INVENTORY),
    NPCS_NAME(PlaceholderCategory.NPC_INVENTORY),
    NPCS_UUID(PlaceholderCategory.NPC_INVENTORY),
    NPCS_NODE(PlaceholderCategory.NPC_INVENTORY),
    NPCS_ENVIRONMENT(PlaceholderCategory.NPC_INVENTORY),
    NPCS_LIFE_CYCLE(PlaceholderCategory.NPC_INVENTORY),
    NPCS_RUNTIME(PlaceholderCategory.NPC_INVENTORY),
    NPCS_HOST(PlaceholderCategory.NPC_INVENTORY),
    NPCS_PORT(PlaceholderCategory.NPC_INVENTORY),
    NPCS_CPU_USAGE(PlaceholderCategory.NPC_INVENTORY),
    NPCS_MEMORY(PlaceholderCategory.NPC_INVENTORY),
    NPCS_THREADS(PlaceholderCategory.NPC_INVENTORY),
    NPCS_ONLINE(PlaceholderCategory.NPC_INVENTORY),
    NPCS_ONLINE_PLAYERS(PlaceholderCategory.NPC_INVENTORY),
    NPCS_MAX_PLAYERS(PlaceholderCategory.NPC_INVENTORY),
    NPCS_MOTD(PlaceholderCategory.NPC_INVENTORY),
    NPCS_EXTRA(PlaceholderCategory.NPC_INVENTORY),
    NPCS_STATE(PlaceholderCategory.NPC_INVENTORY),
    NPCS_TEMPLATE(PlaceholderCategory.NPC_INVENTORY),
    NPCS_VERSION(PlaceholderCategory.NPC_INVENTORY),
    NPCS_WHITELIST(PlaceholderCategory.NPC_INVENTORY),

    NPCS_INFO_GROUP(PlaceholderCategory.NPC_INFO_LINE),
    NPCS_INFO_ONLINE_PLAYERS(PlaceholderCategory.NPC_INFO_LINE),
    NPCS_INFO_MAX_PLAYERS(PlaceholderCategory.NPC_INFO_LINE),
    NPCS_INFO_ONLINE_SERVERS(PlaceholderCategory.NPC_INFO_LINE);

    private final PlaceholderCategory category;

    PlaceholderType(PlaceholderCategory category) {
        this.category = category;
    }

    public PlaceholderCategory getCategory() {
        return this.category;
    }

}
