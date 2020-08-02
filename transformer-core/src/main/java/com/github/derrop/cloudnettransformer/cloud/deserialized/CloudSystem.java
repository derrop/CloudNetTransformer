package com.github.derrop.cloudnettransformer.cloud.deserialized;

import com.github.derrop.cloudnettransformer.cloud.deserialized.database.DatabaseProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageCategory;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.NPCConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.npcs.placed.PlacedNPC;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.group.PermissionConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user.PermissionUserProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.player.PlayerProvider;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.FallbackConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.login.LoginConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist.TabListConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.*;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory.StaticServiceDirectory;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.directory.TemplateDirectory;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.PlacedSign;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.SignConfiguration;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CloudSystem {

    private NPCConfiguration npcConfiguration;
    private SignConfiguration signConfiguration;
    private PermissionConfiguration permissionConfiguration;
    private transient PermissionUserProvider permissionUserProvider;
    private transient PlayerProvider playerProvider;
    private final Collection<StaticServiceDirectory> staticServices = new HashSet<>();
    private CloudConfig config;

    private final Multimap<ServiceEnvironment, TemplateDirectory> globalTemplates = HashMultimap.create();

    private final Map<ServiceEnvironment, Path> applicationFiles = new HashMap<>();

    private final Collection<ServiceTask> tasks = new HashSet<>();
    private final Collection<ServiceGroup> groups = new HashSet<>();
    private final Collection<TemplateDirectory> templates = new HashSet<>();
    private final Collection<PlacedSign> signs = new HashSet<>();
    private final Collection<PlacedNPC> npcs = new HashSet<>();
    private transient DatabaseProvider databaseProvider;

    private final Collection<FallbackConfiguration> fallbackConfigurations = new HashSet<>();
    private final Collection<MotdConfiguration> motdConfigurations = new HashSet<>();
    private final Collection<TabListConfiguration> tabListConfigurations = new HashSet<>();
    private final Collection<LoginConfiguration> loginConfigurations = new HashSet<>();

    private final List<UserNote> notes = new ArrayList<>();
    private final Map<MessageType, String> messages;

    public CloudSystem() {
        this.messages = Arrays.stream(MessageType.values()).collect(Collectors.toMap(Function.identity(), MessageType::getDefaultMessage));
    }

    public void setMessage(MessageType type, String message) {
        this.messages.put(type, message);
    }

    public Map<MessageType, String> getMessages() {
        return this.messages;
    }

    public String getMessage(MessageType type) {
        return this.messages.get(type);
    }

    public Map<MessageType, String> getMessages(MessageCategory category) {
        return this.messages.entrySet().stream()
                .filter(entry -> entry.getKey().getCategory() == category)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<MessageType, String> getMessages(MessageType... types) {
        Collection<MessageType> collection = Arrays.asList(types);
        return this.messages.entrySet().stream()
                .filter(entry -> collection.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public SignConfiguration getSignConfiguration() {
        return this.signConfiguration;
    }

    public void setSignConfiguration(SignConfiguration signConfiguration) {
        this.signConfiguration = signConfiguration;
    }

    public NPCConfiguration getNpcConfiguration() {
        return this.npcConfiguration;
    }

    public void setNpcConfiguration(NPCConfiguration npcConfiguration) {
        this.npcConfiguration = npcConfiguration;
    }

    public CloudConfig getConfig() {
        return this.config;
    }

    public void setConfig(CloudConfig config) {
        this.config = config;
    }

    public PermissionUserProvider getPermissionUserProvider() {
        return this.permissionUserProvider;
    }

    public void setPermissionUserProvider(PermissionUserProvider permissionUserProvider) {
        this.permissionUserProvider = permissionUserProvider;
    }

    public PlayerProvider getPlayerProvider() {
        return this.playerProvider;
    }

    public void setPlayerProvider(PlayerProvider playerProvider) {
        this.playerProvider = playerProvider;
    }

    public PermissionConfiguration getPermissionConfiguration() {
        return this.permissionConfiguration;
    }

    public void setPermissionConfiguration(PermissionConfiguration permissionConfiguration) {
        this.permissionConfiguration = permissionConfiguration;
    }

    public DatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

    public void setDatabaseProvider(DatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    public Collection<PlacedSign> getSigns() {
        return this.signs;
    }

    public Collection<PlacedNPC> getNpcs() {
        return this.npcs;
    }

    public Collection<TemplateDirectory> getTemplates() {
        return this.templates;
    }

    public Collection<StaticServiceDirectory> getStaticServices() {
        return this.staticServices;
    }

    public Collection<ServiceTask> getTasks() {
        return this.tasks;
    }

    public Collection<ServiceGroup> getGroups() {
        return this.groups;
    }

    public void addNote(UserNote note) {
        String[] lines = note.getMessage().split("\n");
        if (lines.length != 1) {
            for (String line : lines) {
                this.notes.add(UserNote.of(note.getLevel(), line).ansi(note.getAnsi()));
            }
            return;
        }

        this.notes.add(note);
    }

    public List<UserNote> getNotes() {
        return this.notes;
    }

    public Collection<FallbackConfiguration> getFallbackConfigurations() {
        return this.fallbackConfigurations;
    }

    public Collection<MotdConfiguration> getMotdConfigurations() {
        return this.motdConfigurations;
    }

    public Collection<TabListConfiguration> getTabListConfigurations() {
        return this.tabListConfigurations;
    }

    public Collection<LoginConfiguration> getLoginConfigurations() {
        return this.loginConfigurations;
    }

    public Collection<ServiceDeployment> getAllDeployments(ServiceTask task) {
        return this.filterForGroups(task, ServiceConfigurationBase::getDeployments);
    }

    public Collection<ServiceTemplate> getAllTemplates(ServiceTask task) {
        return this.filterForGroups(task, ServiceConfigurationBase::getTemplates);
    }

    public Collection<ServiceInclusion> getAllInclusions(ServiceTask task) {
        return this.filterForGroups(task, ServiceConfigurationBase::getInclusions);
    }

    public Collection<String> getAllJvmOptions(ServiceTask task) {
        return this.filterForGroups(task, ServiceConfigurationBase::getJvmOptions);
    }

    private <T> Collection<T> filterForGroups(ServiceTask task, Function<ServiceConfigurationBase, Collection<T>> function) {
        Collection<T> result = new ArrayList<>(function.apply(task));
        for (ServiceGroup group : this.groups) {
            if (task.getGroups().contains(group.getName()) || group.getEnvironments().contains(task.getEnvironment())) {
                result.addAll(function.apply(group));
            }
        }
        return result;
    }

    public void addExcludedServiceFiles(String... excludedFiles) {
        for (TemplateDirectory template : this.templates) {
            template.getExcludedFiles().addAll(Arrays.asList(excludedFiles));
        }
        for (StaticServiceDirectory staticService : this.staticServices) {
            staticService.getExcludedFiles().addAll(Arrays.asList(excludedFiles));
        }
    }

    public void addGlobalTemplate(ServiceEnvironment environment, TemplateDirectory template) {
        this.globalTemplates.put(environment, template);
    }

    public Collection<TemplateDirectory> getGlobalTemplates(ServiceEnvironment environment) {
        Collection<TemplateDirectory> directories = this.globalTemplates.get(environment);
        return directories == null ? Collections.emptyList() : directories;
    }

    public Map<ServiceEnvironment, Path> getApplicationFiles() {
        return this.applicationFiles;
    }

}
