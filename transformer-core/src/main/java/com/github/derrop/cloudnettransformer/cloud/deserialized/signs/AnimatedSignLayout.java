package com.github.derrop.cloudnettransformer.cloud.deserialized.signs;

import java.util.Collection;

public class AnimatedSignLayout {

    private final Collection<SignLayout> signLayouts;
    private final int animationsPerSecond;

    public AnimatedSignLayout(Collection<SignLayout> signLayouts, int animationsPerSecond) {
        this.signLayouts = signLayouts;
        this.animationsPerSecond = animationsPerSecond;
    }

    public Collection<SignLayout> getSignLayouts() {
        return this.signLayouts;
    }

    public int getAnimationsPerSecond() {
        return this.animationsPerSecond;
    }
}
