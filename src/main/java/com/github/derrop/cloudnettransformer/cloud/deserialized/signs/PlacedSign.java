package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

public class PlacedSign {

    private final long creationTime;
    private final String targetGroup;
    private final String placedGroup;
    private final String world;
    private final double x;
    private final double y;
    private final double z;

    public PlacedSign(long creationTime, String targetGroup, String placedGroup, String world, double x, double y, double z) {
        this.creationTime = creationTime;
        this.targetGroup = targetGroup;
        this.placedGroup = placedGroup;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public String getTargetGroup() {
        return this.targetGroup;
    }

    public String getPlacedGroup() {
        return this.placedGroup;
    }

    public String getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
