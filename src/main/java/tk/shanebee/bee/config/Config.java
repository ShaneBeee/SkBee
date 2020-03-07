package tk.shanebee.bee.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tk.shanebee.bee.SkBee;

import java.io.File;

public class Config {

    private final SkBee plugin;
    private FileConfiguration config;
    private File configFile;

    // Config stuff
    public boolean SETTINGS_DEBUG;
    public boolean ELEMENTS_NBT;
    public boolean ELEMENTS_BOARD;
    public boolean ELEMENTS_RECIPE;
    public boolean ELEMENTS_BOUND;
    public boolean ELEMENTS_STRUCTURE;
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
        loadConfigs();
    }

    private void loadConfigs() {
        this.SETTINGS_DEBUG = this.config.getBoolean("settings.debug");
        this.ELEMENTS_NBT = this.config.getBoolean("elements.nbt");
        this.ELEMENTS_BOARD = this.config.getBoolean("elements.scoreboard");
        this.ELEMENTS_RECIPE = this.config.getBoolean("elements.recipe");
        this.ELEMENTS_BOUND = this.config.getBoolean("elements.bound");
        this.ELEMENTS_STRUCTURE = this.config.getBoolean("elements.structure");
        this.RECIPE_NAMESPACE = this.config.getString("recipe.namespace");
    }
}
