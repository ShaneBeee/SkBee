package com.shanebeestudios.skbee.api.profiler;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Profiler {

    private final String type;
    private boolean enabled = true;
    private final Map<String, Profile> profileMap = new TreeMap<>();
    private Profile currentProfile = null;

    public Profiler(String type) {
        this.type = type;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        reset();
    }

    @NotNull
    private Profile getProfile(String name) {
        Profile profile;
        if (this.profileMap.containsKey(name)) {
            profile = this.profileMap.get(name);
        } else {
            profile = new Profile(name, this.type);
            this.profileMap.put(name, profile);
        }
        return profile;
    }

    public void start(String name) {
        if (!this.enabled) return;
        if (this.currentProfile != null) {
            throw new IllegalArgumentException("Current profile still active: " + currentProfile.getName());
        }
        this.currentProfile = getProfile(name);
        this.currentProfile.start();
    }

    public void stop() {
        if (!this.enabled) return;
        if (this.currentProfile == null) {
            throw new IllegalArgumentException("No current profile running");
        }
        this.currentProfile.stop();
        this.currentProfile = null;
    }

    public Collection<Profile> getProfileMap() {
        return this.profileMap.values();
    }

    public void reset() {
        this.profileMap.clear();
    }

}
