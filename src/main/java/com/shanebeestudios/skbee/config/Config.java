package com.shanebeestudios.skbee.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.shanebeestudios.skbee.SkBee;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {

    private final SkBee plugin;
    private FileConfiguration config;
    private File configFile;

    // Config stuff
    public boolean SETTINGS_DEBUG;
    public boolean ELEMENTS_NBT;
    public boolean ELEMENTS_BOARD;
    public boolean ELEMENTS_TEAM;
    public boolean ELEMENTS_RECIPE;
    public boolean ELEMENTS_BOUND;
    public boolean ELEMENTS_STRUCTURE;
    public boolean ELEMENTS_VIRTUAL_FURNACE;
    public boolean ELEMENTS_TEXT_COMPONENT;
    public boolean ELEMENTS_PATHFINDING;
    public boolean ELEMENTS_WORLD_CREATOR;
    public boolean ELEMENTS_GAME_EVENT;
    public boolean ELEMENTS_BOSS_BAR;
    public boolean AUTO_LOAD_WORLDS;
    public String RECIPE_NAMESPACE;

    public Config(SkBee plugin) {
        this.plugin = plugin;
        loadConfigFile();
    }

    private void loadConfigFile() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        matchConfig();
        loadConfigs();
    }

    // Used to update config
    @SuppressWarnings("ConstantConditions")
    private void matchConfig() {
        try {
            boolean hasUpdated = false;
            InputStream stream = plugin.getResource(configFile.getName());
            assert stream != null;
            InputStreamReader is = new InputStreamReader(stream);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(is);
            for (String key : defConfig.getConfigurationSection("").getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defConfig.get(key));
                    hasUpdated = true;
                }
            }
            for (String key : config.getConfigurationSection("").getKeys(true)) {
                if (!defConfig.contains(key)) {
                    config.set(key, null);
                    hasUpdated = true;
                }
            }
            if (hasUpdated)
                config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean getElement(String element) {
        return this.config.getBoolean("elements." + element);
    }

    private void loadConfigs() {
        this.SETTINGS_DEBUG = this.config.getBoolean("settings.debug");
        this.ELEMENTS_NBT = getElement("nbt");
        this.ELEMENTS_BOARD = getElement("scoreboard");
        this.ELEMENTS_TEAM = getElement("team");
        this.ELEMENTS_RECIPE = getElement("recipe");
        this.ELEMENTS_BOUND = getElement("bound");
        this.ELEMENTS_STRUCTURE = getElement("structure");
        this.ELEMENTS_VIRTUAL_FURNACE = getElement("virtual-furnace");
        this.ELEMENTS_TEXT_COMPONENT = getElement("text-component");
        this.ELEMENTS_PATHFINDING = getElement("pathfinding");
        this.ELEMENTS_WORLD_CREATOR = getElement("world-creator");
        this.ELEMENTS_GAME_EVENT = getElement("game-event");
        this.ELEMENTS_BOSS_BAR = getElement("boss-bar");
        this.AUTO_LOAD_WORLDS = getElement("auto-load-custom-worlds");
        String namespace = this.config.getString("recipe.namespace");
        if (namespace == null) {
            namespace = "skbee";
        }
        this.RECIPE_NAMESPACE = namespace.toLowerCase();
    }

}
