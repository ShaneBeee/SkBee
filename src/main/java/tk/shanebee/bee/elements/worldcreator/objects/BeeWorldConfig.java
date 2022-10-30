package tk.shanebee.bee.elements.worldcreator.objects;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BeeWorldConfig {

    private final SkBee plugin;
    private FileConfiguration worldConfig;
    private File worldConfigFile;

    private final Map<String, BeeWorldCreator> WORLDS = new HashMap<>();

    public BeeWorldConfig(SkBee plugin) {
        this.plugin = plugin;
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
            Util.log("Loading custom worlds...");
            for (String key : section.getKeys(false)) {
                BeeWorldCreator beeWorldCreator = loadWorld(key);
                if (beeWorldCreator != null) {
                    WORLDS.put(key, beeWorldCreator);
                }
            }
        }
        int size = WORLDS.size();
        if (size > 0) {
            Util.log("&aSuccessfully loaded &b%s &acustom world%s", size, size > 1 ? "s" : "");
        }
    }

    public BeeWorldCreator loadWorld(String name) {
        String path = "worlds." + name + ".";
        BeeWorldCreator worldCreator = new BeeWorldCreator(name);

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

        if (worldCreator.loadWorld() != null) {
            return worldCreator;
        }
        return null;
    }

    public void saveWorldToFile(BeeWorldCreator worldCreator) {
        if (!WORLDS.containsKey(worldCreator.getWorldName())) {
            WORLDS.put(worldCreator.getWorldName(), worldCreator);
        }
        String path = "worlds." + worldCreator.getWorldName() + ".";
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

}
