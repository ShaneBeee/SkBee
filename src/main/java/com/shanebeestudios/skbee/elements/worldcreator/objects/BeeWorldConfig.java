package com.shanebeestudios.skbee.elements.worldcreator.objects;

import ch.njol.skript.test.runner.TestMode;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("CallToPrintStackTrace")
public class BeeWorldConfig {

    private final SkBee plugin;
    private FileConfiguration worldConfig;
    private File worldConfigFile;
    private final boolean autoLoadWorlds;

    private final Map<String, BeeWorldCreator> WORLDS = new HashMap<>();

    public BeeWorldConfig(SkBee plugin) {
        this.plugin = plugin;
        this.autoLoadWorlds = plugin.getPluginConfig().AUTO_LOAD_WORLDS;
        loadConfig();
    }

    private void loadConfig() {
        if (worldConfigFile == null) {
            worldConfigFile = new File(plugin.getDataFolder(), "worlds.yml");
        }
        if (!worldConfigFile.exists()) {
            plugin.saveResource("worlds.yml", false);
        }
        worldConfig = YamlConfiguration.loadConfiguration(worldConfigFile);
    }

    public void loadCustomWorlds() {
        ConfigurationSection section = worldConfig.getConfigurationSection("worlds");
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            if (keys.isEmpty()) {
                return;
            }
            Util.log("&6Loading custom worlds...");
            int loadedWorlds = 0;
            for (String key : keys) {
                BeeWorldCreator beeWorldCreator = loadWorld(key);
                if (beeWorldCreator != null) {
                    WORLDS.put(key, beeWorldCreator);
                    if (beeWorldCreator.isLoaded) loadedWorlds++;
                }
            }
            Util.log("&aSuccessfully loaded &b%s &acustom world%s", loadedWorlds, loadedWorlds == 1 ? "" : "s");
        }
    }

    public @Nullable BeeWorldCreator loadWorld(String name) {
        String path = "worlds." + name + ".";
        String keyString = worldConfig.getString(path + "key");
        NamespacedKey key = keyString != null ? Util.getNamespacedKey(keyString, false) : null;
        BeeWorldCreator worldCreator = new BeeWorldCreator(name, key);

        String type = worldConfig.getString(path + "type");
        if (type != null) {
            worldCreator.setWorldType(getWorldType(type));
        }

        String environment = worldConfig.getString(path + "environment");
        if (environment != null) {
            worldCreator.setEnvironment(getEnvironment(environment));
        }

        if (worldConfig.isSet(path + "seed")) {
            long seed = worldConfig.getLong(path + "seed");
            worldCreator.setSeed(seed);
        }

        if (worldConfig.isSet(path + "generator-settings")) {
            worldCreator.setGeneratorSettings(worldConfig.getString(path + "generator-settings"));
        }

        if (worldConfig.isSet(path + "generator")) {
            worldCreator.setGenerator(worldConfig.getString(path + "generator"));
        }

        if (worldConfig.isSet(path + "structures")) {
            worldCreator.setGenStructures(worldConfig.getBoolean(path + "structures"));
        }

        if (worldConfig.isSet(path + "hardcore")) {
            worldCreator.setHardcore(worldConfig.getBoolean(path + "hardcore"));
        }

        if (worldConfig.isSet(path + "keep-spawn-loaded")) {
            worldCreator.setKeepSpawnLoaded(worldConfig.getBoolean(path + "keep-spawn-loaded"));
        }

        if (worldConfig.isSet(path + "load-on-start")) {
            boolean loadOnStart = worldConfig.getBoolean(path + "load-on-start");
            worldCreator.setLoadOnStart(loadOnStart);

            // return but don't load the world
            if (!loadOnStart) return worldCreator;
        } else if (!autoLoadWorlds) {
            // return but don't load the world
            return worldCreator;
        }

        if (worldCreator.loadWorld() != null) {
            worldCreator.isLoaded = true;
            return worldCreator;
        }
        return null;
    }

    public void saveWorldToFile(BeeWorldCreator worldCreator) {
        if (!WORLDS.containsKey(worldCreator.getWorldName())) {
            WORLDS.put(worldCreator.getWorldName(), worldCreator);
        }
        String path = "worlds." + worldCreator.getWorldName() + ".";
        worldConfig.set(path + "key", worldCreator.getKey().toString());
        worldConfig.set(path + "type", worldCreator.getWorldType().toString());
        worldConfig.set(path + "environment", worldCreator.getEnvironment().toString());
        worldConfig.set(path + "seed", worldCreator.seed);
        if (worldCreator.getGeneratorSettings() != null) {
            worldConfig.set(path + "generator-settings", worldCreator.getGeneratorSettings());
        }
        if (worldCreator.getGenerator() != null) {
            worldConfig.set(path + "generator", worldCreator.getGenerator());
        }

        worldCreator.genStructures.ifPresent(aBoolean -> worldConfig.set(path + "structures", aBoolean));
        worldCreator.hardcore.ifPresent(aBoolean -> worldConfig.set(path + "hardcore", aBoolean));
        worldCreator.keepSpawnLoaded.ifPresent(aBoolean -> worldConfig.set(path + "keep-spawn-loaded", aBoolean));
        worldCreator.loadOnStart.ifPresent(aBoolean -> worldConfig.set(path + "load-on-start", aBoolean));

        save();
    }

    public void deleteWorld(String worldName) {
        // Only delete custom worlds, and make sure it's not the plugins folder
        if (WORLDS.containsKey(worldName) && !worldName.equalsIgnoreCase("plugins")) {
            WORLDS.remove(worldName);
            worldConfig.set("worlds." + worldName, null);
            save();

            File worldFile = new File(Bukkit.getWorldContainer(), worldName);
            if (worldFile.exists() && worldFile.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(worldFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void save() {
        if (TestMode.ENABLED) return; // Don't save during test mode
        try {
            worldConfig.save(worldConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WorldType getWorldType(String name) {
        try {
            return WorldType.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignore) {
            Util.skriptError("Invalid world type: " + name.toUpperCase(Locale.ROOT));
            Util.skriptError(" - &7Default to NORMAL");
            return WorldType.NORMAL;
        }
    }

    private Environment getEnvironment(String name) {
        try {
            return Environment.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignore) {
            Util.skriptError("Invalid world environment: " + name.toUpperCase(Locale.ROOT));
            Util.skriptError(" - &7Default to NORMAL");
            return Environment.NORMAL;
        }
    }

    /**
     * Get all loaded custom worlds
     *
     * @return All loaded custom worlds
     */
    public List<World> getLoadedCustomWorlds() {
        List<World> worlds = new ArrayList<>();
        WORLDS.forEach((string, beeWorldCreator) -> {
            World world = Bukkit.getWorld(beeWorldCreator.getWorldName());
            if (world != null) worlds.add(world);
        });
        return worlds;
    }

}
