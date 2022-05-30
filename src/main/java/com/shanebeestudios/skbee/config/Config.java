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

    private void loadConfigs() {
        this.SETTINGS_DEBUG = this.config.getBoolean("settings.debug");
        this.ELEMENTS_NBT = this.config.getBoolean("elements.nbt");
        this.ELEMENTS_BOARD = this.config.getBoolean("elements.scoreboard");
        this.ELEMENTS_TEAM = this.config.getBoolean("elements.team");
        this.ELEMENTS_RECIPE = this.config.getBoolean("elements.recipe");
        this.ELEMENTS_BOUND = this.config.getBoolean("elements.bound");
        this.ELEMENTS_STRUCTURE = this.config.getBoolean("elements.structure");
        this.ELEMENTS_VIRTUAL_FURNACE = this.config.getBoolean("elements.virtual-furnace");
        this.ELEMENTS_TEXT_COMPONENT = this.config.getBoolean("elements.text-component");
        this.ELEMENTS_PATHFINDING = this.config.getBoolean("elements.pathfinding");
        this.ELEMENTS_WORLD_CREATOR = this.config.getBoolean("elements.world-creator");
        this.ELEMENTS_GAME_EVENT = this.config.getBoolean("elements.game-event");
        this.ELEMENTS_BOSS_BAR = this.config.getBoolean("elements.boss-bar");
        this.AUTO_LOAD_WORLDS = this.config.getBoolean("elements.auto-load-custom-worlds");
        String namespace = this.config.getString("recipe.namespace");
        if (namespace == null) {
            namespace = "skbee";
        }
        this.RECIPE_NAMESPACE = namespace.toLowerCase();
    }
}
