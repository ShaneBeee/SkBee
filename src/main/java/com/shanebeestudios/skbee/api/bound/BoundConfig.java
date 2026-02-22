package com.shanebeestudios.skbee.api.bound;

import ch.njol.skript.Skript;
import ch.njol.skript.test.runner.TestMode;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.region.scheduler.Scheduler;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
public class BoundConfig implements Listener {

    private final File boundFile;
    private final FileConfiguration boundConfig;
    private final BoundPostponing boundPostponing;
    private final Map<String, Bound> boundsMap = new HashMap<>();
    private final Map<NamespacedKey, BoundWorld> boundWorldMap = new HashMap<>();

    // Maps scheduled to remove/save later
    private final Map<String, Bound> scheduledToSave = new HashMap<>();
    private final List<String> scheduledToRemove = new ArrayList<>();

    /**
     * @hidden
     */
    public BoundConfig(SkBee plugin) {
        this.boundPostponing = new BoundPostponing(this);
        // Load config
        this.boundFile = new File(plugin.getDataFolder(), "bounds.yml");
        if (!this.boundFile.exists()) {
            plugin.saveResource("bounds.yml", false);
        }
        this.boundConfig = YamlConfiguration.loadConfiguration(this.boundFile);

        // Load bounds
        ConfigurationSection section = this.boundConfig.getConfigurationSection("bounds");
        if (section != null) {
            for (String key : section.getKeys(true)) {
                Object object = section.get(key);
                if (object instanceof Bound bound) {
                    if (bound.getWorld() == null) {
                        this.boundPostponing.postponeLoading(bound);
                    } else {
                        if (bound.getWorldKey() == null) {
                            bound.updateKey();
                        }
                        addBoundToRegionAndMap(bound);
                    }
                }
            }
        }
        this.boundPostponing.print();
        // Only start save timer if not in test mode
        if (!TestMode.ENABLED) startSaveTimer();
    }

    private void startSaveTimer() {
        Scheduler<?> globalScheduler = TaskUtils.getGlobalScheduler();
        globalScheduler.runTaskTimer(() -> {
            // Skip saving if maps are empty
            if (this.scheduledToSave.isEmpty() && this.scheduledToRemove.isEmpty()) return;

            // Remove cached bounds from yaml
            this.scheduledToRemove.forEach(id -> this.boundConfig.set("bounds." + id, null));
            this.scheduledToRemove.clear();

            // Save cached bounds to yaml
            this.scheduledToSave.forEach((id, bound) -> this.boundConfig.set("bounds." + id, bound));
            this.scheduledToSave.clear();

            // Async save yaml to file
            globalScheduler.runTaskAsync(this::saveConfig);
        }, 6000, 6000); // Every 5 minutes
    }

    /**
     * Save a bound to regions, map and config
     * <br>Will be scheduled to save to config later
     *
     * @param bound         Bound to save
     * @param updateRegions Whether to update in regions
     *                      (this can be false when just updating
     *                      specifics of a bound rather than its shape)
     */
    public void saveBound(Bound bound, boolean updateRegions) {
        if (updateRegions) {
            removeBoundFromRegion(bound);
            addBoundToRegionAndMap(bound);
        }
        if (!bound.isTemporary()) {
            this.scheduledToSave.put(bound.getId(), bound);
        }
    }

    /**
     * Remove a bound
     * <br>Will be schedule to remove from config later
     * <br>Will remove from regions, map and config
     *
     * @param bound Bound to remove
     */
    public void removeBound(Bound bound) {
        removeBoundFromRegionAndMap(bound);
        if (!bound.isTemporary()) {
            this.scheduledToRemove.add(bound.getId());
        }
    }

    /**
     * Check if a bound ID already exists
     *
     * @param id ID to check
     * @return True if exists, else false
     */
    public boolean boundExists(String id) {
        return this.boundsMap.containsKey(id);
    }

    /**
     * Get a bound from ID
     *
     * @param id ID of bound to grab
     * @return Bound from ID or null if not available
     */
    public @Nullable Bound getBoundFromID(String id) {
        if (this.boundsMap.containsKey(id))
            return this.boundsMap.get(id);
        return null;
    }

    /**
     * Save all bounds to file
     * <br>This is only used when the server stops
     */
    public void saveAllBoundsOnShutdown() {
        if (!this.scheduledToRemove.isEmpty()) {
            this.scheduledToRemove.forEach(id -> this.boundConfig.set("bounds." + id, null));
            this.scheduledToRemove.clear();
        }
        for (Bound bound : this.boundsMap.values()) {
            if (bound.isTemporary()) continue;
            this.boundConfig.set("bounds." + bound.getId(), bound);
        }
        saveConfig();
    }

    private void saveConfig() {
        try {
            this.boundConfig.save(this.boundFile);
        } catch (IOException e) {
            throw Skript.exception(e);
        }
    }

    /**
     * Get all bounds
     *
     * @return List of all bounds
     */
    public Collection<Bound> getBounds() {
        return this.boundsMap.values();
    }

    /**
     * Get all the bounds in a world
     *
     * @param world World to grab bounds from
     * @return List of bounds in a world
     */
    public Collection<Bound> getBoundsIn(World world) {
        return this.boundsMap.values().stream().filter(bound -> world.equals(bound.getWorld())).toList();
    }

    /**
     * Get all the bounds at a specific location
     *
     * @param location Location to check for bounds
     * @return Bounds at location
     */
    public Collection<Bound> getBoundsAt(Location location) {
        return getBoundsInRegion(location).stream().filter(bound -> bound.isInRegion(location)).toList();
    }

    /**
     * Get a BoundWorld for a world, or create one if it doesn't exist
     *
     * @param worldKey World to reference BoundWorld to
     * @return BoundWorld for a world
     */
    public @NotNull BoundWorld getOrCreateBoundWorld(NamespacedKey worldKey) {
        BoundWorld boundWorld = this.boundWorldMap.get(worldKey);
        if (boundWorld == null) {
            boundWorld = new BoundWorld(worldKey);
            this.boundWorldMap.put(worldKey, boundWorld);
        }
        return boundWorld;
    }

    /**
     * Get a BoundWorld for a world
     *
     * @param worldKey World to reference BoundWorld to
     * @return BoundWorld if it exists, else null
     */
    public @Nullable BoundWorld getBoundWorld(NamespacedKey worldKey) {
        return this.boundWorldMap.get(worldKey);
    }

    /**
     * Get all the bounds in a region at a specific location
     *
     * @param location Location to check for bounds
     * @return List of bounds in region
     */
    public List<Bound> getBoundsInRegion(Location location) {
        BoundWorld boundWorld = getOrCreateBoundWorld(location.getWorld().getKey());
        return boundWorld.getBoundsAtLocation(location);
    }

    /**
     * Add a bound to region and bound map
     *
     * @param bound Bound to add
     */
    public void addBoundToRegionAndMap(Bound bound) {
        BoundWorld boundWorld = getOrCreateBoundWorld(bound.getWorldKey());
        boundWorld.addBoundToRegion(bound);
        this.boundsMap.put(bound.getId(), bound);
    }

    /**
     * Remove a bound from its region
     * <br>This will NOT remove it from the bound map
     *
     * @param bound Bound to remove
     */
    public void removeBoundFromRegion(Bound bound) {
        BoundWorld boundWorld = getBoundWorld(bound.getWorldKey());
        if (boundWorld != null) {
            boundWorld.removeBoundFromRegion(bound);
            if (boundWorld.isEmpty()) {
                this.boundWorldMap.remove(bound.getWorldKey());
            }
        }
    }

    /**
     * Remove a bound from its region and the bound map
     *
     * @param bound Bound to remove
     */
    public void removeBoundFromRegionAndMap(Bound bound) {
        removeBoundFromRegion(bound);
        this.boundsMap.remove(bound.getId());
    }

}
