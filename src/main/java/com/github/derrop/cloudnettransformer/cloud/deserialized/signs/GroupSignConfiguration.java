package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

import java.util.Collection;

public class GroupSignConfiguration {

    private final String targetGroup;
    private final boolean hideFullServers;
    private final double knockbackDistance;
    private final double knockbackStrength;
    private final Collection<TaskSignLayout> taskLayouts;
    private final TaskSignLayout globalLayout;
    private final AnimatedSignLayout searchLayout;
    private final AnimatedSignLayout startingLayout;

    public GroupSignConfiguration(String targetGroup, boolean hideFullServers,
                                  double knockbackDistance, double knockbackStrength,
                                  Collection<TaskSignLayout> taskLayouts, TaskSignLayout globalLayout,
                                  AnimatedSignLayout searchLayout, AnimatedSignLayout startingLayout) {
        this.targetGroup = targetGroup;
        this.hideFullServers = hideFullServers;
        this.knockbackDistance = knockbackDistance;
        this.knockbackStrength = knockbackStrength;
        this.taskLayouts = taskLayouts;
        this.globalLayout = globalLayout;
        this.searchLayout = searchLayout;
        this.startingLayout = startingLayout;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }

    public boolean isHideFullServers() {
        return this.hideFullServers;
    }

    public double getKnockbackDistance() {
        return this.knockbackDistance;
    }

    public double getKnockbackStrength() {
        return this.knockbackStrength;
    }

    public Collection<TaskSignLayout> getTaskLayouts() {
        return this.taskLayouts;
    }

    public TaskSignLayout getGlobalLayout() {
        return this.globalLayout;
    }

    public AnimatedSignLayout getSearchLayout() {
        return this.searchLayout;
    }

    public AnimatedSignLayout getStartingLayout() {
        return this.startingLayout;
    }

}
