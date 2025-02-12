package com.shanebeestudios.skbee.api.bound;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
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

    private final Map<String, List<Bound>> postponedBounds = new HashMap<>();

    /**
     * Postpone loading a bound until it's world has loaded
     *
     * @param bound Bound to postpone loading
     */
    public void postponeLoading(Bound bound) {
        String worldName = bound.getWorldName();
        this.postponedBounds.computeIfAbsent(worldName, k -> new ArrayList<>()).add(bound);
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        String name = event.getWorld().getName();
        if (this.postponedBounds.containsKey(name)) {
            int size = this.postponedBounds.get(name).size();
            Util.log("&ePostponed bound loading of %s bounds for world '%s'", size, name);
            this.postponedBounds.get(name).forEach(this.boundConfig::addBoundToRegionAndMap);
            this.postponedBounds.remove(name);
        }
    }

    /**
     * Print a message to console stating delays in bound loading
     */
    public void print() {
        if (!this.postponedBounds.isEmpty()) {
            this.postponedBounds.keySet().forEach(world -> {
                int size = this.postponedBounds.get(world).size();
                Util.log("&ePostponing bound loading of %s bounds for unavailable world '%s'", size, world);
            });
        }
    }

}
