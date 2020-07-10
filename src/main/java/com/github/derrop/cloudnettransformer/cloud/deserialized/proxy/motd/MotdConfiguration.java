package com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.motd;

import java.util.Collection;

public class MotdConfiguration {

    private final String targetGroup;
    private final Collection<MotdLayout> motdLayouts;
    private final Collection<MotdLayout> maintenanceLayouts;

    public MotdConfiguration(String targetGroup, Collection<MotdLayout> motdLayouts, Collection<MotdLayout> maintenanceLayouts) {
        this.targetGroup = targetGroup;
        this.motdLayouts = motdLayouts;
        this.maintenanceLayouts = maintenanceLayouts;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }

    public Collection<MotdLayout> getMotdLayouts() {
        return this.motdLayouts;
    }

    public Collection<MotdLayout> getMaintenanceLayouts() {
        return this.maintenanceLayouts;
    }
}
