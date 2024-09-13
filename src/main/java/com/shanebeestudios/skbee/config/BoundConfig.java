package com.shanebeestudios.skbee.config;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BoundConfig {

    private final SkBee plugin;
    private File boundFile;
    private FileConfiguration boundConfig;
    private final Map<String, Bound> boundsMap = new HashMap<>();

    /**
     * @hidden
     */
    public BoundConfig(SkBee plugin) {
        this.plugin = plugin;
        loadBoundConfig();
    }

    private void loadBoundConfig() {
        if (boundFile == null) {
            boundFile = new File(plugin.getDataFolder(), "bounds.yml");
        }
        if (!boundFile.exists()) {
            plugin.saveResource("bounds.yml", false);
        }
        boundConfig = YamlConfiguration.loadConfiguration(boundFile);
        loadBounds();
    }

    private void loadBounds() {
        ConfigurationSection section = boundConfig.getConfigurationSection("bounds");
        if (section == null) return;
        for (String key : section.getKeys(true)) {
            Object bound = section.get(key);
            if (bound instanceof Bound) {
                boundsMap.put(key, ((Bound) bound));
            }
        }
    }

    public void saveBound(Bound bound) {
        boundsMap.put(bound.getId(), bound);
        if (!bound.isTemporary()) {
            boundConfig.set("bounds." + bound.getId(), bound);
            saveConfig();
        }
    }

    public void removeBound(Bound bound) {
        boundsMap.remove(bound.getId());
        if (!bound.isTemporary()) {
            boundConfig.set("bounds." + bound.getId(), null);
            saveConfig();
        }
    }

    public boolean boundExists(String id) {
        return boundsMap.containsKey(id);
    }

    public @Nullable Bound getBoundFromID(String id) {
        if (boundsMap.containsKey(id))
            return boundsMap.get(id);
        return null;
    }

    public void saveAllBounds() {
        for (Bound bound : boundsMap.values()) {
            if (bound.isTemporary()) continue;
            boundConfig.set("bounds." + bound.getId(), bound);
            boundsMap.put(bound.getId(), bound);
        }
        saveConfig();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void saveConfig() {
        try {
            boundConfig.save(boundFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<Bound> getBounds() {
        return boundsMap.values();
    }

    public Collection<Bound> getBoundsIn(World world) {
        return boundsMap.values().stream().filter(bound -> {
            World boundWorld = bound.getWorld();
            return boundWorld != null && boundWorld.equals(world);
        }).toList();
    }

    public Collection<Bound> getBoundsAt(Location location) {
        return boundsMap.values().stream().filter(bound -> bound.isInRegion(location)).toList();
    }

}
