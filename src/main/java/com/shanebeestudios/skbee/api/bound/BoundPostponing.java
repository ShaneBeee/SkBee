package com.shanebeestudios.skbee.api.bound;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle postponing bound loading
 */
public class BoundPostponing implements Listener {

    private final BoundConfig boundConfig;

    public BoundPostponing(BoundConfig boundConfig) {
        this.boundConfig = boundConfig;
        Bukkit.getPluginManager().registerEvents(this, SkBee.getPlugin());
    }

    @Deprecated(forRemoval = true)
    private final Map<String, List<Bound>> postponedBoundsByName = new HashMap<>();

    private final Map<NamespacedKey, List<Bound>> postponedBounds = new HashMap<>();

    /**
     * Postpone loading a bound until it's world has loaded
     *
     * @param bound Bound to postpone loading
     */
    public void postponeLoading(Bound bound) {
        NamespacedKey worldKey = bound.getWorldKey();
        if (worldKey != null) {
            this.postponedBounds.computeIfAbsent(worldKey, key -> new ArrayList<>()).add(bound);
        } else {
            String worldName = bound.getWorldName();
            if (worldName != null) {
                this.postponedBoundsByName.computeIfAbsent(worldName, k -> new ArrayList<>()).add(bound);
            }
        }
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        // TODO remove in the future (Feb 18/2026)
        String name = event.getWorld().getName();
        if (this.postponedBoundsByName.containsKey(name)) {
            int size = this.postponedBoundsByName.get(name).size();
            Util.log("&ePostponed bound loading of %s bounds for world '%s'", size, name);
            this.postponedBoundsByName.get(name).forEach(bound -> {
                bound.updateKey();
                this.boundConfig.addBoundToRegionAndMap(bound);
            });
            this.postponedBoundsByName.remove(name);
        }

        NamespacedKey key = event.getWorld().getKey();
        if (this.postponedBounds.containsKey(key)) {
            int size = this.postponedBounds.get(key).size();
            Util.log("&ePostponed bound loading of %s bounds for world '%s'", size, key);
            this.postponedBounds.get(key).forEach(this.boundConfig::addBoundToRegionAndMap);
            this.postponedBounds.remove(key);
        }
    }

    /**
     * Print a message to console stating delays in bound loading
     */
    public void print() {
        // TODO remove in the future (Feb 18/2026)
        if (!this.postponedBoundsByName.isEmpty()) {
            this.postponedBoundsByName.keySet().forEach(world -> {
                int size = this.postponedBoundsByName.get(world).size();
                Util.log("&ePostponing bound loading of %s bounds for unavailable world '%s'", size, world);
            });
        }

        if (!this.postponedBounds.isEmpty()) {
            this.postponedBounds.keySet().forEach(world -> {
                int size = this.postponedBounds.get(world).size();
                Util.log("&ePostponing bound loading of %s bounds for unavailable world '%s'", size, world);
            });
        }
    }

}
