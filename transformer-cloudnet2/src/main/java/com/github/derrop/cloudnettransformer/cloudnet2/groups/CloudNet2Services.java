package com.github.derrop.cloudnettransformer.cloudnet2.groups;

import com.github.derrop.cloudnettransformer.Constants;
import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.UserNote;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.PlaceholderType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.Permission;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUser;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.Fallback;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.FallbackConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.login.LoginConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdLayout;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist.TabList;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist.TabListConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceEnvironment;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTask;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceTemplate;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.TemplateInstallerType;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.ExecuteResult;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.ExecutorPriority;
import com.github.derrop.cloudnettransformer.cloudnet2.CloudNet2Utils;
import com.github.derrop.cloudnettransformer.util.StringUtils;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@DescribedCloudExecutor(name = "Services", priority = ExecutorPriority.LAST)
public class CloudNet2Services implements CloudReaderWriter {

    private static final UUID ADMIN_USER_ID = UUID.randomUUID();
    private static final String ADMIN_USER_NAME = "admin-" + StringUtils.randomString(8);

    private Path config(Path directory) {
        return directory.resolve(Constants.MASTER_DIRECTORY).resolve("services.json");
    }

    @Override
    public ExecuteResult write(CloudSystem cloudSystem, Path directory) {
        Document document = Documents.newDocument()
                .append("wrapper", this.writeWrapper(cloudSystem))
                .append("proxyGroups", this.writeProxyGroups(cloudSystem));

        document.json().write(this.config(directory));;

        return ExecuteResult.success();
    }

    private Collection<Document> writeWrapper(CloudSystem cloudSystem) {
        if (!cloudSystem.getPermissionUserProvider().containsUser(ADMIN_USER_ID)) {
            String password = StringUtils.randomString(32);
            cloudSystem.getPermissionUserProvider().insertUser(new PermissionUser(
                    ADMIN_USER_NAME,
                    Collections.singletonList(new Permission("*", 1, -1, null)),
                    ADMIN_USER_ID,
                    null, null,
                    StringUtils.encryptToSHA256Base64(password),
                    Collections.emptyList()
            ));
            cloudSystem.addNote(UserNote.important("Password for the administrative user '" + ADMIN_USER_NAME + "': " + password));
        }
        return Collections.singletonList(Documents.newDocument()
                .append("id", cloudSystem.getConfig().getComponentName())
                .append("hostName", cloudSystem.getConfig().getIp())
                .append("user", ADMIN_USER_NAME)
        );
    }

    private Collection<Document> writeProxyGroups(CloudSystem cloudSystem) {
        Collection<Document> proxyGroups = new ArrayList<>();

        for (ServiceTask task : cloudSystem.getTasks()) {
            if (task.getEnvironment() != ServiceEnvironment.BUNGEECORD) {
                continue;
            }

            Collection<ServiceTemplate> templates = cloudSystem.getAllTemplates(task);
            if (templates.isEmpty()) {
                System.err.println("Not writing ProxyGroup " + task.getName() + " because there is no template!");
                continue;
            }

            ServiceTemplate mainTemplate = new ServiceTemplate("local", task.getName(), templates.iterator().next().getName());

            Document document = Documents.newDocument()
                    .append("name", task.getName())
                    .append("wrapper", task.getNodes())
                    .append("template", CloudNet2Utils.templateToJson(cloudSystem, task, mainTemplate, true))
                    .append("proxyVersion", "BUNGEECORD")
                    .append("startPort", task.getStartPort())
                    .append("startup", task.getMinServices())
                    .append("memory", task.getMaxMemory())
                    .append("proxyGroupMode", task.isStaticServices() ? "STATIC" : "DYNAMIC");

            Document proxyConfig = Documents.newDocument().append("customPayloadFixer", false);

            Document emptyMotd = Documents.newDocument().append("firstLine", "").append("secondLine", "");

            proxyConfig
                    .append("enabled", false)
                    .append("maintenance", false)
                    .append("motdsLayouts", Collections.singletonList(emptyMotd))
                    .append("maintenanceMotdLayout", emptyMotd)
                    .append("maintenaceProtocol", "")
                    .append("maxPlayers", 100)
                    .append("customPayloadFixer", false)
                    .append("autoSlot", Documents.newDocument().append("enabled", false).append("dynamicSlotSize", 1))
                    .append("tabList", Documents.newDocument().append("enabled", false).append("header", "").append("footer", ""))
                    .append("playerInfo", Collections.emptyList())
                    .append("whitelist", Collections.emptyList())
                    .append("dynamicFallback", Documents.newDocument().append("defaultFallback", "Lobby").append("fallbacks", Collections.emptyList()));

            cloudSystem.getLoginConfigurations().stream().filter(config -> config.getTargetGroup().equals(task.getName())).findFirst()
                    .ifPresent(loginConfiguration -> this.writeLoginConfiguration(proxyConfig, loginConfiguration));

            cloudSystem.getTabListConfigurations().stream().filter(config -> config.getTargetGroup().equals(task.getName())).findFirst()
                    .ifPresent(tabListConfiguration -> this.writeTabListConfiguration(cloudSystem, proxyConfig, tabListConfiguration));

            cloudSystem.getMotdConfigurations().stream().filter(config -> config.getTargetGroup().equals(task.getName())).findFirst()
                    .ifPresent(motdConfiguration -> this.writeMotdConfiguration(cloudSystem, proxyConfig, motdConfiguration));

            cloudSystem.getFallbackConfigurations().stream().filter(config -> config.getTargetGroup().equals(task.getName())).findFirst()
                    .ifPresent(fallbackConfiguration -> this.writeFallbackConfiguration(proxyConfig, fallbackConfiguration));

            document.append("proxyConfig", proxyConfig).append("settings", task.getProperties());

            proxyGroups.add(document);
        }

        return proxyGroups;
    }

    private void writeLoginConfiguration(Document proxyConfig, LoginConfiguration loginConfiguration) {
        proxyConfig.append("enabled", true);

        proxyConfig
                .append("maintenance", loginConfiguration.isMaintenance())
                .append("maxPlayers", loginConfiguration.getMaxPlayers())
                .append("whitelist", loginConfiguration.getWhitelist());
    }

    private void writeTabListConfiguration(CloudSystem cloudSystem, Document proxyConfig, TabListConfiguration tabListConfiguration) {
        if (!tabListConfiguration.getEntries().isEmpty()) {
            proxyConfig.append("enabled", true);
        }

        TabList tabList = tabListConfiguration.getEntries().isEmpty() ? null : tabListConfiguration.getEntries().iterator().next();

        proxyConfig.append("tabList",
                Documents.newDocument()
                        .append("enabled", tabList != null)
                        .append("header", this.updateTabPlaceholders(cloudSystem, tabList != null ? tabList.getHeader() : ""))
                        .append("footer", this.updateTabPlaceholders(cloudSystem, tabList != null ? tabList.getFooter() : ""))
        );
    }

    private void writeMotdConfiguration(CloudSystem cloudSystem, Document proxyConfig, MotdConfiguration motdConfiguration) {
        proxyConfig.append("enabled", true);

        MotdLayout maintenanceMotd = motdConfiguration.getMaintenanceLayouts().isEmpty() ? null : motdConfiguration.getMaintenanceLayouts().iterator().next();
        proxyConfig.append("maintenanceMotdLayout",
                Documents.newDocument()
                        .append("firstLine", maintenanceMotd != null ? maintenanceMotd.getFirstLine() : "")
                        .append("secondLine", maintenanceMotd != null ? maintenanceMotd.getSecondLine() : "")
        );

        Collection<Document> motdLayouts = new ArrayList<>();

        if (motdConfiguration.getMotdLayouts().isEmpty()) {
            motdLayouts.add(Documents.newDocument().append("firstLine", "").append("secondLine", ""));
        } else {
            for (MotdLayout motdLayout : motdConfiguration.getMotdLayouts()) {
                proxyConfig
                        .append("playerInfo", motdLayout.getPlayerInfo())
                        .append("autoSlot", Documents.newDocument().append("enabled", motdLayout.isAutoSlot()).append("dynamicSlotSize", motdLayout.getAutoSlotDistance()));

                motdLayouts.add(Documents.newDocument()
                        .append("firstLine", this.updateLoginPlaceholders(cloudSystem, motdLayout.getFirstLine()))
                        .append("secondLine", this.updateLoginPlaceholders(cloudSystem, motdLayout.getSecondLine()))
                );
            }
        }

        proxyConfig.append("motdsLayouts", motdLayouts);
    }

    private void writeFallbackConfiguration(Document proxyConfig, FallbackConfiguration fallbackConfiguration) {
        Document dynamicFallback = Documents.newDocument();

        Collection<Document> fallbacks = new ArrayList<>(fallbackConfiguration.getFallbacks().size());
        fallbackConfiguration.getFallbacks().stream()
                .sorted(Comparator.comparingInt(Fallback::getPriority))
                .filter(fallback -> fallback.getPermission() != null)
                .forEach(fallback -> fallbacks.add(Documents.newDocument().append("group", fallback.getTask()).append("permission", fallback.getPermission())));

        dynamicFallback
                .append("defaultFallback", fallbackConfiguration.getDefaultFallback())
                .append("fallbacks", fallbacks);

        proxyConfig.append("dynamicFallback", dynamicFallback);
    }

    private String updateTabPlaceholders(CloudSystem cloudSystem, String input) {
        Map<PlaceholderType, String> map = new HashMap<>();
        this.fillTabPlaceholders(map);
        return cloudSystem.updatePlaceholders(input, map);
    }

    private String updateLoginPlaceholders(CloudSystem cloudSystem, String input) {
        Map<PlaceholderType, String> map = new HashMap<>();
        this.fillLoginPlaceholders(map);
        return cloudSystem.updatePlaceholders(input, map);
    }

    private void fillTabPlaceholders(Map<PlaceholderType, String> map) {
        map.put(PlaceholderType.TAB_PROXY, "%proxy%");
        map.put(PlaceholderType.TAB_SERVER, "%server%");
        map.put(PlaceholderType.TAB_ONLINE_PLAYERS, "%online_players%");
        map.put(PlaceholderType.TAB_MAX_PLAYERS, "%max_players%");
        map.put(PlaceholderType.TAB_SERVER_TASK, "%group%");
        map.put(PlaceholderType.TAB_PROXY_TASK, "%proxy_group%");
    }

    private void fillLoginPlaceholders(Map<PlaceholderType, String> map) {
        map.put(PlaceholderType.MOTD_PROXY, "%proxy%");
        map.put(PlaceholderType.MOTD_VERSION, "%version%");
    }

    @Override
    public ExecuteResult read(CloudSystem cloudSystem, Path directory) {

        Path configPath = this.config(directory);
        if (Files.notExists(configPath)) {
            return ExecuteResult.failed("No services.json found as " + configPath);
        }

        Document document = Documents.jsonStorage().read(configPath);

        Collection<Document> wrapper = document.getDocuments("wrapper");
        Collection<Document> proxyGroups = document.getDocuments("proxyGroups");

        this.fillTabPlaceholders(cloudSystem.getPlaceholders());
        this.fillLoginPlaceholders(cloudSystem.getPlaceholders());

        if (wrapper != null && proxyGroups != null) {
            this.readWrapper(cloudSystem, wrapper);
            this.readProxyGroups(cloudSystem, proxyGroups);
            return ExecuteResult.success();
        }

        return ExecuteResult.failed("No wrapper/proxyGroups arrays set in the services.json");
    }

    private void readWrapper(CloudSystem cloudSystem, Collection<Document> wrapper) {
        // this transformer doesn't support a multi wrapper setup
    }

    private void readProxyGroups(CloudSystem cloudSystem, Collection<Document> proxyGroups) {
        for (Document proxyGroup : proxyGroups) {

            Document template = proxyGroup.getDocument("template");

            ServiceTask task = new ServiceTask(
                    proxyGroup.getString("name"),
                    Collections.singletonList(
                            new ServiceTemplate("local", proxyGroup.getString("name"), template.getString("name"))
                    ),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    template.get("processPreParameters", TypeToken.getParameterized(Collection.class, String.class).getType()),
                    false,
                    proxyGroup.getString("proxyGroupMode").equals("STATIC"),
                    proxyGroup.get("wrapper", TypeToken.getParameterized(Collection.class, String.class).getType()),
                    Arrays.asList(Constants.GLOBAL_PROXY_GROUP, proxyGroup.getString("name")),
                    proxyGroup.getInt("memory"),
                    proxyGroup.getInt("startPort"),
                    proxyGroup.getInt("startup"),
                    -1,
                    -1,
                    ServiceEnvironment.BUNGEECORD,
                    TemplateInstallerType.ALL,
                    Documents.newDocument()
            );

            cloudSystem.getTasks().add(task);

            Document proxyConfig = proxyGroup.getDocument("proxyConfig");
            if (proxyConfig != null) {
                this.readProxyConfig(cloudSystem, task, proxyConfig);
            }
        }
    }

    private void readProxyConfig(CloudSystem cloudSystem, ServiceTask task, Document proxyConfig) {
        Document fallback = proxyConfig.getDocument("dynamicFallback");
        if (fallback != null) {
            Collection<Document> fallbackDocs = fallback.getDocuments("fallbacks");
            Collection<Fallback> fallbacks = new ArrayList<>(fallbackDocs.size());
            int priority = fallbackDocs.size();
            for (Document fallbackDoc : fallbackDocs) {
                fallbacks.add(new Fallback(fallbackDoc.getString("group"), fallbackDoc.getString("permission"), priority--));
            }

            FallbackConfiguration fallbackConfiguration = new FallbackConfiguration(task.getName(), fallback.getString("defaultFallback"), fallbacks);
            cloudSystem.getFallbackConfigurations().add(fallbackConfiguration);
        }

        if (!proxyConfig.getBoolean("enabled")) {
            return;
        }

        Document autoSlot = proxyConfig.getDocument("autoSlot");
        boolean autoSlotEnabled = autoSlot != null && autoSlot.getBoolean("enabled");
        int autoSlotDistance = autoSlot != null ? autoSlot.getInt("dynamicSlotSize") : 0;
        String[] playerInfo = proxyConfig.get("playerInfo", String[].class);

        Document maintenanceMotd = proxyConfig.getDocument("maintenanceMotdLayout");

        LoginConfiguration loginConfiguration = new LoginConfiguration(
                task.getName(),
                proxyConfig.getBoolean("maintenance"),
                proxyConfig.getInt("maxPlayers"),
                proxyConfig.get("whitelist", TypeToken.getParameterized(Collection.class, String.class).getType())
        );
        MotdConfiguration motdConfiguration = new MotdConfiguration(
                task.getName(),
                proxyConfig.getDocuments("motdsLayouts").stream()
                        .map(document -> new MotdLayout(
                                document.getString("firstLine"), document.getString("secondLine"),
                                autoSlotEnabled, autoSlotDistance,
                                playerInfo,
                                null
                        )).collect(Collectors.toList()),
                Collections.singletonList(
                        new MotdLayout(
                                maintenanceMotd.getString("firstLine"),
                                maintenanceMotd.getString("secondLine"),
                                autoSlotEnabled, autoSlotDistance,
                                playerInfo,
                                proxyConfig.getString("maintenaceProtocol")
                        )
                )
        );

        Document tabList = proxyConfig.getDocument("tabList");
        if (tabList != null && tabList.getBoolean("enabled")) {
            TabListConfiguration tabListConfiguration = new TabListConfiguration(
                    task.getName(),
                    Collections.singletonList(new TabList(tabList.getString("header"), tabList.getString("footer"))),
                    1
            );
            cloudSystem.getTabListConfigurations().add(tabListConfiguration);
        }

        cloudSystem.getLoginConfigurations().add(loginConfiguration);
        cloudSystem.getMotdConfigurations().add(motdConfiguration);
    }

}
