package com.shanebeestudios.skbee.elements.bound.config;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.util.WorldUtils;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BoundConfig {

    private static final String UPDATED_18_HEIGHTS = "updated_18_heights";

    private final SkBee plugin;
    private File boundFile;
    private FileConfiguration boundConfig;
    private final Map<String, Bound> boundsMap = new HashMap<>();

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

        // Update heights for 1.18 worlds
        if (Skript.isRunningMinecraft(1, 18) && !boundConfig.getBoolean(UPDATED_18_HEIGHTS)) {
            update18Heights();
        }
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

    private void update18Heights() {
        Util.log("Updating bounds:");
        for (Bound bound : boundsMap.values()) {
            int lesserY = bound.getLesserY();
            int greaterY = bound.getGreaterY();
            World world = bound.getWorld();
            if (lesserY == 0 && world != null) {
                int minHeight = WorldUtils.getMinHeight(world);
                int maxHeight = WorldUtils.getMaxHeight(world);
                if (greaterY == 255 || greaterY == maxHeight) {
                    bound.setGreaterY(maxHeight);
                    bound.setLesserY(minHeight);
                    Util.log("Updating bound with id '%s'", bound.getId());
                }
            }
        }
        boundConfig.set(UPDATED_18_HEIGHTS, true);
        saveAllBounds();
    }

    public void saveBound(Bound bound) {
        boundConfig.set("bounds." + bound.getId(), bound);
        boundsMap.put(bound.getId(), bound);
        saveConfig();
    }

    public void removeBound(Bound bound) {
        boundsMap.remove(bound.getId());
        boundConfig.set("bounds." + bound.getId(), null);
        saveConfig();
    }

    public Bound getBoundFromID(String id) {
        if (boundsMap.containsKey(id))
            return boundsMap.get(id);
        return null;
    }

    public void saveAllBounds() {
        for (Bound bound : boundsMap.values()) {
            boundConfig.set("bounds." + bound.getId(), bound);
            boundsMap.put(bound.getId(), bound);
        }
        saveConfig();
    }

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

}
