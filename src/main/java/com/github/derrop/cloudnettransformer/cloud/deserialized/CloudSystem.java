package com.github.derrop.cloudnettransformer.cloud.deserialized;

import com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.PermissionConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.login.LoginConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.fallback.FallbackConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd.MotdConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist.TabListConfiguration;
import com.github.derrop.cloudnettransformer.cloud.deserialized.service.*;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.SignConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class CloudSystem {

    private SignConfiguration signConfiguration;
    private PermissionConfiguration permissionConfiguration;
    private final Collection<ServiceTask> tasks = new ArrayList<>();
    private final Collection<ServiceGroup> groups = new ArrayList<>();

    private final Collection<FallbackConfiguration> fallbackConfigurations = new ArrayList<>();
    private final Collection<MotdConfiguration> motdConfigurations = new ArrayList<>();
    private final Collection<TabListConfiguration> tabListConfigurations = new ArrayList<>();
    private final Collection<LoginConfiguration> loginConfigurations = new ArrayList<>();

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
