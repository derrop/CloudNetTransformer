package com.github.derrop.cloudnettransformer.cloud.deserialized;

import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageCategory;
import com.github.derrop.cloudnettransformer.cloud.deserialized.message.MessageType;
import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.PermissionConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.FallbackConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.login.LoginConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist.TabListConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.*;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.SignConfiguration;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CloudSystem {

    private SignConfiguration signConfiguration;
    private PermissionConfiguration permissionConfiguration;
    private final Collection<ServiceTask> tasks = new ArrayList<>();
    private final Collection<ServiceGroup> groups = new ArrayList<>();

    private final Collection<FallbackConfiguration> fallbackConfigurations = new ArrayList<>();
    private final Collection<MotdConfiguration> motdConfigurations = new ArrayList<>();
    private final Collection<TabListConfiguration> tabListConfigurations = new ArrayList<>();
    private final Collection<LoginConfiguration> loginConfigurations = new ArrayList<>();

    private final Map<MessageType, String> messages = new HashMap<>();

    public CloudSystem() {
        for (MessageType type : MessageType.values()) {
            this.messages.put(type, type.getDefaultMessage());
        }
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

    public PermissionConfiguration getPermissionConfiguration() {
        return this.permissionConfiguration;
    }

    public void setPermissionConfiguration(PermissionConfiguration permissionConfiguration) {
        this.permissionConfiguration = permissionConfiguration;
    }

    public Collection<ServiceTask> getTasks() {
        return this.tasks;
    }

    public Collection<ServiceGroup> getGroups() {
        return this.groups;
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

}
