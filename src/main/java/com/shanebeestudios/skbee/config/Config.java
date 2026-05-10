package com.shanebeestudios.skbee.config;

import ch.njol.skript.Skript;
import ch.njol.skript.test.runner.TestMode;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {

    private static Boolean IS_TESTING = null;

    private final SkBee plugin;
    private FileConfiguration config;
    private File configFile;

    // Config stuff
    public boolean settings_debug;
    public boolean settings_use_paper_schedulers;
    public boolean settings_update_checker_enabled;
    public boolean settings_update_checker_async;
    public boolean settings_fastboard_lines;
    public boolean elements_nbt;
    public boolean nbt_allow_unsafe_operations;
    public boolean nbt_allow_force_load_unknown_version;
    public boolean nbt_events_break_block;
    public boolean nbt_events_piston_extend;
    public boolean nbt_events_entity_change_block;
    public boolean nbt_events_entity_explode;
    public boolean nbt_events_block_explode;
    public boolean elements_bound;
    public boolean bound_events_player_move;
    public boolean bound_events_player_teleport;
    public boolean bound_events_player_respawn;
    public boolean bound_events_player_bed_enter;
    public boolean bound_events_player_bed_leave;
    public boolean bound_events_entity_mount;
    public boolean bound_events_entity_dismount;
    public boolean bound_events_vehicle_enter;
    public boolean bound_events_vehicle_exit;
    public boolean bound_events_vehicle_destroy;
    public boolean bound_events_vehicle_move;
    public boolean elements_fastboard;
    public boolean elements_scoreboard;
    public boolean elements_recipe;
    public boolean elements_structure;
    public boolean elements_virtual_furnace;
    public boolean elements_world_creator;
    public boolean elements_game_event;
    public boolean elements_boss_bar;
    public boolean elements_statistic;
    public boolean elements_villager;
    public boolean elements_advancement;
    public boolean elements_raytrace;
    public boolean elements_fishing;
    public boolean elements_tick_manager;
    public boolean elements_damage_source;
    public boolean elements_item_component;
    public boolean elements_switch_case;
    public boolean elements_property;
    public boolean elements_dialog;
    public boolean auto_load_worlds;
    public boolean runtime_disable_errors;
    public boolean runtime_disable_warnings;

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
        if (isTestingEnabled()) return true;
        return this.config.getBoolean("elements." + element);
    }

    private boolean getNBTEvent(String nbtEvent) {
        return this.config.getBoolean("nbt-events." + nbtEvent);
    }

    private boolean getBoundEvent(String boundEvent) {
        return this.config.getBoolean("bound-events." + boundEvent);
    }

    private void loadConfigs() {
        this.settings_debug = getSetting("debug");
        this.settings_use_paper_schedulers = getSetting("use-paper-schedulers") && Skript.classExists("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
        if (this.settings_use_paper_schedulers) {
            Util.logLoading("&eUsing Paper schedulers.");
        }
        this.settings_update_checker_enabled = getSetting("update-checker.enabled");
        this.settings_update_checker_async = getSetting("update-checker.async");

        this.settings_fastboard_lines = getSetting("fastboard-reverse-lines");

        this.elements_nbt = getElement("nbt");
        this.nbt_allow_force_load_unknown_version = getSetting("allow-nbt-force-load-unknown-version");
        this.nbt_allow_unsafe_operations = getSetting("allow-unsafe-nbt-operations");
        if (this.nbt_allow_unsafe_operations) {
            Util.logLoading("&eAllow Unsafe NBT Operations enabled!");
        }
        this.nbt_events_break_block = getNBTEvent("block-break");
        this.nbt_events_piston_extend = getNBTEvent("piston-extend");
        this.nbt_events_entity_change_block = getNBTEvent("entity-change-block");
        this.nbt_events_entity_explode = getNBTEvent("entity-explode");
        this.nbt_events_block_explode = getNBTEvent("block-explode");

        this.elements_bound = getElement("bound");
        this.bound_events_player_move = getBoundEvent("player-move");
        this.bound_events_player_teleport = getBoundEvent("player-teleport");
        this.bound_events_player_respawn = getBoundEvent("player-respawn");
        this.bound_events_player_bed_enter = getBoundEvent("player-bed-enter");
        this.bound_events_player_bed_leave = getBoundEvent("player-bed-leave");
        this.bound_events_entity_mount = getBoundEvent("entity-mount");
        this.bound_events_entity_dismount = getBoundEvent("entity-dismount");
        this.bound_events_vehicle_enter = getBoundEvent("vehicle-enter");
        this.bound_events_vehicle_exit = getBoundEvent("vehicle-exit");
        this.bound_events_vehicle_move = getBoundEvent("vehicle-move");
        this.bound_events_vehicle_destroy = getBoundEvent("vehicle-destroy");

        this.elements_fastboard = getElement("fastboard");
        this.elements_scoreboard = getElement("scoreboard");
        this.elements_recipe = getElement("recipe");
        this.elements_structure = getElement("structure");
        this.elements_virtual_furnace = getElement("virtual-furnace");
        this.elements_world_creator = getElement("world-creator");
        this.elements_game_event = getElement("game-event");
        this.elements_boss_bar = getElement("boss-bar");
        this.elements_statistic = getElement("statistic");
        this.elements_villager = getElement("villager");
        this.elements_advancement = getElement("advancement");
        this.elements_raytrace = getElement("raytrace");
        this.elements_fishing = getElement("fishing");
        this.elements_tick_manager = getElement("tick-manager");
        this.elements_damage_source = getElement("damage-source");
        this.elements_item_component = getElement("item-component");
        this.elements_switch_case = getElement("switch-case");
        this.elements_property = getElement("property");
        this.elements_dialog = getElement("dialog");
        this.auto_load_worlds = getElement("auto-load-custom-worlds");

        this.runtime_disable_errors = this.config.getBoolean("runtime.disable-errors");
        this.runtime_disable_warnings = this.config.getBoolean("runtime.disable-warnings");
    }

    // Prevents NoClassDefFoundError error when using outdated Skript version before AddonLoader checks version
    private static boolean isTestingEnabled() {
        if (IS_TESTING == null) {
            if (Bukkit.getPluginManager().getPlugin("Skript") != null && Skript.classExists("ch.njol.skript.test.runner.TestMode")) {
                IS_TESTING = TestMode.ENABLED;
            } else {
                IS_TESTING = false;
            }
        }
        return IS_TESTING;
    }

}
