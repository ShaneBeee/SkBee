package com.shanebeestudios.skbee.api.worldgen;

import ch.njol.skript.test.runner.TestMode;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.util.legacy.LegacyUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
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

    private final Map<NamespacedKey, BeeWorldCreator> WORLDS = new HashMap<>();

    public BeeWorldConfig(SkBee plugin) {
        this.plugin = plugin;
        this.autoLoadWorlds = plugin.getPluginConfig().auto_load_worlds;
        loadConfig();
    }

    private void loadConfig() {
        if (this.worldConfigFile == null) {
            worldConfigFile = new File(plugin.getDataFolder(), "worlds.yml");
        }
        if (!worldConfigFile.exists()) {
            plugin.saveResource("worlds.yml", false);
        }
        worldConfig = YamlConfiguration.loadConfiguration(worldConfigFile);
    }

    public void loadCustomWorlds() {
        ConfigurationSection section = this.worldConfig.getConfigurationSection("worlds");
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            if (keys.isEmpty()) {
                return;
            }
            Util.log("&6Loading custom worlds...");
            int loadedWorlds = 0;
            for (String pathKey : keys) {
                BeeWorldCreator beeWorldCreator = loadWorld(pathKey);
                if (beeWorldCreator != null) {
                    WORLDS.put(beeWorldCreator.getKey(), beeWorldCreator);
                    if (beeWorldCreator.isLoaded) loadedWorlds++;
                }
            }
            Util.log("&aSuccessfully loaded &b%s &acustom world%s", loadedWorlds, loadedWorlds == 1 ? "" : "s");
        }
    }

    public @Nullable BeeWorldCreator loadWorld(String pathKey) {
        String path = "worlds." + pathKey + ".";
        String name = null;
        NamespacedKey key;
        if (pathKey.contains(":")) {
            key = Util.getNamespacedKey(pathKey, false);
        } else {
            name = pathKey;
            String keyString = this.worldConfig.getString(path + "key");
            key = keyString != null ? Util.getNamespacedKey(keyString, false) : null;
        }
        BeeWorldCreator worldCreator = new BeeWorldCreator(null, name, key, false);

        String type = worldConfig.getString(path + "type");
        if (type != null) {
            worldCreator.setWorldType(getWorldType(type));
        }

        String environment = worldConfig.getString(path + "environment");
        if (environment != null) {
            worldCreator.setEnvironment(getEnvironment(environment));
        }

        if (this.worldConfig.isSet(path + "seed")) {
            long seed = worldConfig.getLong(path + "seed");
            worldCreator.setSeed(seed);
        }

        if (this.worldConfig.isSet(path + "generator-settings")) {
            worldCreator.setGeneratorSettings(worldConfig.getString(path + "generator-settings"));
        }

        if (this.worldConfig.isSet(path + "generator")) {
            worldCreator.setGenerator(worldConfig.getString(path + "generator"));
        }

        if (this.worldConfig.isSet(path + "structures")) {
            worldCreator.setGenStructures(worldConfig.getBoolean(path + "structures"));
        }

        if (this.worldConfig.isSet(path + "hardcore")) {
            worldCreator.setHardcore(worldConfig.getBoolean(path + "hardcore"));
        }

        if (this.worldConfig.isSet(path + "custom-chunk-generator")) {
            String genKey = this.worldConfig.getString(path + "custom-chunk-generator.key");
            boolean hasBiome = this.worldConfig.getBoolean(path + "custom-chunk-generator.has-biome-provider");
            worldCreator.setChunkGenerator(ChunkGenManager.registerOrGetGenerator(genKey, hasBiome));
        }

        if (this.worldConfig.isSet(path + "load-on-start")) {
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
        NamespacedKey key = worldCreator.getKey();
        if (!WORLDS.containsKey(key)) {
            WORLDS.put(key, worldCreator);
        }
        String pathString;
        if (LegacyUtils.IS_RUNNING_MC_26_1_1) {
            // Clear out old world in config
            if (this.worldConfig.isSet("worlds." + worldCreator.getWorldName())) {
                this.worldConfig.set("worlds." + worldCreator.getWorldName(), null);
            }
            pathString = key.toString();
        } else {
            pathString = worldCreator.getWorldName();
        }
        String path = "worlds." + pathString + ".";
        if (!LegacyUtils.IS_RUNNING_MC_26_1_1) {
            this.worldConfig.set(path + "key", key.toString());
        }
        this.worldConfig.set(path + "type", worldCreator.getWorldType().toString());
        this.worldConfig.set(path + "environment", worldCreator.getEnvironment().toString());
        this.worldConfig.set(path + "seed", worldCreator.getSeed());
        if (worldCreator.getGeneratorSettings() != null) {
            this.worldConfig.set(path + "generator-settings", worldCreator.getGeneratorSettings());
        }
        if (worldCreator.getGenerator() != null) {
            this.worldConfig.set(path + "generator", worldCreator.getGenerator());
        }

        worldCreator.genStructures.ifPresent(aBoolean -> this.worldConfig.set(path + "structures", aBoolean));
        worldCreator.hardcore.ifPresent(aBoolean -> this.worldConfig.set(path + "hardcore", aBoolean));
        worldCreator.loadOnStart.ifPresent(aBoolean -> this.worldConfig.set(path + "load-on-start", aBoolean));

        ChunkGenerator chunkGenerator = worldCreator.getChunkGenerator();
        if (chunkGenerator instanceof CustomChunkGenerator customChunkGenerator) {
            this.worldConfig.set(path + "custom-chunk-generator.key", customChunkGenerator.getKey());
            this.worldConfig.set(path + "custom-chunk-generator.has-biome-provider", customChunkGenerator.hasBiomeProvider());
        }

        save();
    }

    @Deprecated(forRemoval = true, since = "3.20.0")
    public void deleteWorld(String worldName) {
        // Only delete custom worlds, and make sure it's not the plugins folder
        if (this.worldConfig.isSet("worlds." + worldName) && !worldName.equals("plugins")) {
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

    @SuppressWarnings("UnstableApiUsage")
    public void deleteWorld(NamespacedKey worldKey) {
        // Only delete custom worlds, and make sure it's not the plugins folder
        if (WORLDS.containsKey(worldKey)) {
            WORLDS.remove(worldKey);
            worldConfig.set("worlds." + worldKey, null);
            save();

            File worldDirectory = Bukkit.getServer().getLevelDirectory().toFile();
            File dimensions = new File(worldDirectory, "dimensions");
            File namespace = new File(dimensions, worldKey.namespace());
            if (!namespace.exists() || !namespace.isDirectory()) return;

            File dimension = new File(namespace, worldKey.getKey());
            if (dimension.exists() && dimension.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(dimension);
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
            World world = Bukkit.getWorld(beeWorldCreator.getKey());
            if (world != null) worlds.add(world);
        });
        return worlds;
    }

}
