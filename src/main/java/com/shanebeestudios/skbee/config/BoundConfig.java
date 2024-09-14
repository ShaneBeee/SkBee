package com.shanebeestudios.skbee.config;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoundConfig {

    private final File boundFile;
    private final FileConfiguration boundConfig;
    private final Map<String, Bound> boundsMap = new HashMap<>();
    private final Map<World, BoundWorld> boundWorldMap = new HashMap<>();

    // Maps scheduled to remove/save later
    private final Map<String, Bound> scheduledToSave = new HashMap<>();
    private final List<String> scheduledToRemove = new ArrayList<>();

    /**
     * @hidden
     */
    public BoundConfig(SkBee plugin) {
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
                    addBoundToRegionAndMap(bound);
                }
            }
        }
        startSaveTimer(plugin);
    }

    private void startSaveTimer(SkBee plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskTimer(plugin, () -> {
            // Skip saving if maps are empty
            if (this.scheduledToSave.isEmpty() && this.scheduledToRemove.isEmpty()) return;

            // Remove cached bounds from yaml
            this.scheduledToRemove.forEach(id -> this.boundConfig.set("bounds." + id, null));
            this.scheduledToRemove.clear();

            // Save cached bounds to yaml
            this.scheduledToSave.forEach((id, bound) -> this.boundConfig.set("bounds." + id, bound));
            this.scheduledToSave.clear();

            // Async save yaml to file
            scheduler.runTaskAsynchronously(plugin, this::saveConfig);
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
    public void saveAllBounds() {
        for (Bound bound : this.boundsMap.values()) {
            if (bound.isTemporary()) continue;
            this.boundConfig.set("bounds." + bound.getId(), bound);
        }
        saveConfig();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void saveConfig() {
        try {
            this.boundConfig.save(this.boundFile);
        } catch (IOException e) {
            e.printStackTrace();
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
     * @param world World to reference BoundWorld to
     * @return BoundWorld for a world
     */
    public @NotNull BoundWorld getOrCreateBoundWorld(World world) {
        BoundWorld boundWorld = this.boundWorldMap.get(world);
        if (boundWorld == null) {
            boundWorld = new BoundWorld(world);
            this.boundWorldMap.put(world, boundWorld);
        }
        return boundWorld;
    }

    /**
     * Get a BoundWorld for a world
     *
     * @param world World to reference BoundWorld to
     * @return BoundWorld if it exists, else null
     */
    public @Nullable BoundWorld getBoundWorld(World world) {
        return this.boundWorldMap.get(world);
    }

    /**
     * Get all the bounds in a region at a specific location
     *
     * @param location Location to check for bounds
     * @return List of bounds in region
     */
    public List<Bound> getBoundsInRegion(Location location) {
        BoundWorld boundWorld = getOrCreateBoundWorld(location.getWorld());
        return boundWorld.getBoundsAtLocation(location);
    }

    /**
     * Add a bound to region and bound map
     *
     * @param bound Bound to add
     */
    public void addBoundToRegionAndMap(Bound bound) {
        BoundWorld boundWorld = getOrCreateBoundWorld(bound.getWorld());
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
        BoundWorld boundWorld = getBoundWorld(bound.getWorld());
        if (boundWorld != null) {
            boundWorld.removeBoundFromRegion(bound);
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
