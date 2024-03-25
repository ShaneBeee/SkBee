package com.shanebeestudios.skbee.api.profiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Profilers {

    private static final Map<String, Profiler> PROFILER_MAP = new TreeMap<>();
    public static final Profiler EFFECT = register("effect");
    public static final Profiler SECTION = register("section");
    public static final Profiler CODE = register("code");

    private static Profiler register(String type) {
        Profiler profiler = new Profiler(type);
        PROFILER_MAP.put(type, profiler);
        return profiler;
    }

    @NotNull
    public static Collection<Profiler> getAllProfilers() {
        return PROFILER_MAP.values();
    }

    public static Set<String> getTypes() {
        return PROFILER_MAP.keySet();
    }

    @Nullable
    public static Profiler getByType(String type) {
        if (PROFILER_MAP.containsKey(type)) return PROFILER_MAP.get(type);
        return null;
    }

    public static void setEnabled(boolean enabled) {
        PROFILER_MAP.values().forEach(profiler -> profiler.setEnabled(enabled));
    }

}
