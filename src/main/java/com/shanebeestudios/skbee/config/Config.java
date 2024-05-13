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
    public boolean SETTINGS_UPDATE_CHECKER_ENABLED;
    public boolean SETTINGS_UPDATE_CHECKER_ASYNC;
    public String SETTINGS_NAMESPACE;
    public boolean SETTINGS_SCOREBOARD_LINES;
    public boolean ELEMENTS_NBT;
    public boolean NBT_ADMIN_ERRORS;
    public boolean NBT_EVENTS_BREAK_BLOCK;
    public boolean NBT_EVENTS_PISTON_EXTEND;
    public boolean NBT_EVENTS_ENTITY_CHANGE_BLOCK;
    public boolean NBT_EVENTS_ENTITY_EXPLODE;
    public boolean NBT_EVENTS_BLOCK_EXPLODE;
    public boolean ELEMENTS_BOUND;
    public boolean BOUND_EVENTS_PLAYER_MOVE;
    public boolean BOUND_EVENTS_PLAYER_TELEPORT;
    public boolean BOUND_EVENTS_PLAYER_RESPAWN;
    public boolean BOUND_EVENTS_PLAYER_BED_ENTER;
    public boolean BOUND_EVENTS_PLAYER_BED_LEAVE;
    public boolean BOUND_EVENTS_ENTITY_MOUNT;
    public boolean BOUND_EVENTS_ENTITY_DISMOUNT;
    public boolean BOUND_EVENTS_VEHICLE_ENTER;
    public boolean BOUND_EVENTS_VEHICLE_EXIT;
    public boolean BOUND_EVENTS_VEHICLE_DESTROY;
    public boolean BOUND_EVENTS_VEHICLE_MOVE;
    public boolean ELEMENTS_BOARD;
    public boolean ELEMENTS_OBJECTIVE;
    public boolean ELEMENTS_TEAM;
    public boolean ELEMENTS_RECIPE;
    public boolean ELEMENTS_STRUCTURE;
    public boolean ELEMENTS_VIRTUAL_FURNACE;
    public boolean ELEMENTS_TEXT_COMPONENT;
    public boolean ELEMENTS_WORLD_CREATOR;
    public boolean ELEMENTS_GAME_EVENT;
    public boolean ELEMENTS_BOSS_BAR;
    public boolean ELEMENTS_STATISTIC;
    public boolean ELEMENTS_VILLAGER;
    public boolean ELEMENTS_ADVANCEMENT;
    public boolean ELEMENTS_WORLD_BORDER;
    public boolean ELEMENTS_PARTICLE;
    public boolean ELEMENTS_MINECRAFT_TAG;
    public boolean ELEMENTS_RAYTRACE;
    public boolean ELEMENTS_FISHING;
    public boolean ELEMENTS_DISPLAY;
    public boolean ELEMENTS_TICK_MANAGER;
    public boolean ELEMENTS_DAMAGE_SOURCE;
    public boolean ELEMENTS_CHUNK_GEN;
    public boolean AUTO_LOAD_WORLDS;

    /**
     * @hidden
     */
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

    private boolean getSetting(String setting) {
        return this.config.getBoolean("settings." + setting);
    }

    private boolean getElement(String element) {
        return this.config.getBoolean("elements." + element);
    }

    private boolean getNBTEvent(String nbtEvent) {
        return this.config.getBoolean("nbt-events." + nbtEvent);
    }

    private boolean getBoundEvent(String boundEvent) {
        return this.config.getBoolean("bound-events." + boundEvent);
    }

    private void loadConfigs() {
        this.SETTINGS_DEBUG = getSetting("debug");
        this.SETTINGS_UPDATE_CHECKER_ENABLED = getSetting("update-checker.enabled");
        this.SETTINGS_UPDATE_CHECKER_ASYNC = getSetting("update-checker.async");

        String namespace = this.config.getString("settings.namespace");
        this.SETTINGS_NAMESPACE = namespace != null ? namespace.toLowerCase() : "skbee";
        this.SETTINGS_SCOREBOARD_LINES = getSetting("scoreboard-reverse-lines");

        this.ELEMENTS_NBT = getElement("nbt");
        this.NBT_ADMIN_ERRORS = getElement("nbt-admin-errors");
        this.NBT_EVENTS_BREAK_BLOCK = getNBTEvent("block-break");
        this.NBT_EVENTS_PISTON_EXTEND = getNBTEvent("piston-extend");
        this.NBT_EVENTS_ENTITY_CHANGE_BLOCK = getNBTEvent("entity-change-block");
        this.NBT_EVENTS_ENTITY_EXPLODE = getNBTEvent("entity-explode");
        this.NBT_EVENTS_BLOCK_EXPLODE = getNBTEvent("block-explode");

        this.ELEMENTS_BOUND = getElement("bound");
        this.BOUND_EVENTS_PLAYER_MOVE = getBoundEvent("player-move");
        this.BOUND_EVENTS_PLAYER_TELEPORT = getBoundEvent("player-teleport");
        this.BOUND_EVENTS_PLAYER_RESPAWN = getBoundEvent("player-respawn");
        this.BOUND_EVENTS_PLAYER_BED_ENTER = getBoundEvent("player-bed-enter");
        this.BOUND_EVENTS_PLAYER_BED_LEAVE = getBoundEvent("player-bed-leave");
        this.BOUND_EVENTS_ENTITY_MOUNT = getBoundEvent("entity-mount");
        this.BOUND_EVENTS_ENTITY_DISMOUNT = getBoundEvent("entity-dismount");
        this.BOUND_EVENTS_VEHICLE_ENTER = getBoundEvent("vehicle-enter");
        this.BOUND_EVENTS_VEHICLE_EXIT = getBoundEvent("vehicle-exit");
        this.BOUND_EVENTS_VEHICLE_MOVE = getBoundEvent("vehicle-move");
        this.BOUND_EVENTS_VEHICLE_DESTROY = getBoundEvent("vehicle-destroy");

        this.ELEMENTS_BOARD = getElement("scoreboard");
        this.ELEMENTS_OBJECTIVE = getElement("scoreboard-objective");
        this.ELEMENTS_TEAM = getElement("team");
        this.ELEMENTS_RECIPE = getElement("recipe");
        this.ELEMENTS_STRUCTURE = getElement("structure");
        this.ELEMENTS_VIRTUAL_FURNACE = getElement("virtual-furnace");
        this.ELEMENTS_TEXT_COMPONENT = getElement("text-component");
        this.ELEMENTS_WORLD_CREATOR = getElement("world-creator");
        this.ELEMENTS_GAME_EVENT = getElement("game-event");
        this.ELEMENTS_BOSS_BAR = getElement("boss-bar");
        this.ELEMENTS_STATISTIC = getElement("statistic");
        this.ELEMENTS_VILLAGER = getElement("villager");
        this.ELEMENTS_ADVANCEMENT = getElement("advancement");
        this.ELEMENTS_WORLD_BORDER = getElement("world-border");
        this.ELEMENTS_PARTICLE = getElement("particle");
        this.ELEMENTS_MINECRAFT_TAG = getElement("minecraft-tag");
        this.ELEMENTS_RAYTRACE = getElement("raytrace");
        this.ELEMENTS_FISHING = getElement("fishing");
        this.ELEMENTS_DISPLAY = getElement("display-entity");
        this.ELEMENTS_TICK_MANAGER = getElement("tick-manager");
        this.ELEMENTS_DAMAGE_SOURCE = getElement("damage-source");
        this.ELEMENTS_CHUNK_GEN = getElement("chunk-generator");
        this.AUTO_LOAD_WORLDS = getElement("auto-load-custom-worlds");
    }

}
