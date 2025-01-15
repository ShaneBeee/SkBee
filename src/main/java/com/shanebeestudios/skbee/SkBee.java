package com.shanebeestudios.skbee;

import ch.njol.skript.Skript;
import ch.njol.skript.test.runner.TestMode;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.command.SkBeeInfo;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.util.UpdateChecker;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.LazyLocation;
import com.shanebeestudios.skbee.config.BoundConfig;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.other.sections.SecRunTaskLater;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldConfig;
import com.shanebeestudios.vf.api.VirtualFurnaceAPI;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for SkBee
 */
public class SkBee extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(Bound.class, "Bound");
        ConfigurationSerialization.registerClass(LazyLocation.class, "LazyLocation");
    }

    // Earliest MC Version that SkBee will support
    static final int[] EARLIEST_VERSION = new int[]{1, 18, 2};

    private static SkBee instance;
    private boolean properlyEnabled = true;
    private Config config;
    BoundConfig boundConfig = null;
    VirtualFurnaceAPI virtualFurnaceAPI;
    BeeWorldConfig beeWorldConfig;
    StructureManager structureManager = null;
    private AddonLoader addonLoader = null;

    /**
     * @hidden must be public for Bukkit but let's hide from docs
     */
    public SkBee() {
    }

    /**
     * @hidden
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        // Let's get this party started...
        long start = System.currentTimeMillis();
        instance = this;
        this.config = new Config(this);
        PluginManager pm = Bukkit.getPluginManager();

        this.addonLoader = new AddonLoader(this);
        // Check if SkriptAddon can actually load
        this.properlyEnabled = addonLoader.canLoadPlugin();

        loadCommands();
        loadMetrics();

        // Beta check + notice
        String version = getDescription().getVersion();
        if (version.contains("-")) {
            Util.log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
            Util.log("&ehttps://github.com/ShaneBeee/SkBee/issues");
        }

        new UpdateChecker(this);
        Util.log("&aSuccessfully enabled v%s&7 in &b%.2f seconds", version, (float) (System.currentTimeMillis() - start) / 1000);

        // Load custom worlds if enabled in config
        if (this.properlyEnabled && this.beeWorldConfig != null) {
            this.beeWorldConfig.loadCustomWorlds();
        }
        // Looks like we made it after all
    }


    private void loadCommands() {
        //noinspection ConstantConditions
        getCommand("skbee").setExecutor(new SkBeeInfo(this));
        //pm.registerEvents(new ScriptListener(), this); // Temp removed
    }

    private void loadMetrics() { //6719
        Metrics metrics = new Metrics(this, 6719);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
        metrics.addCustomChart(new SimplePie("virtual_furnace", () -> String.valueOf(config.ELEMENTS_VIRTUAL_FURNACE)));
    }

    /**
     * @hidden
     */
    @Override
    public void onDisable() {
        if (this.properlyEnabled) {
            // Cancel tasks on stop to prevent async issues
            SecRunTaskLater.cancelTasks();
        }
        if (this.virtualFurnaceAPI != null) {
            this.virtualFurnaceAPI.disableAPI();
        }
        // Only save bounds if not in test mode
        if (!TestMode.ENABLED && this.boundConfig != null) {
            this.boundConfig.saveAllBounds();
        }
    }

    /**
     * Get an instance of this plugin
     *
     * @return Instance of this plugin
     */
    public static SkBee getPlugin() {
        return instance;
    }

    /**
     * Get an instance of this plugin's {@link Config}
     *
     * @return Instance of this plugin's config
     */
    public Config getPluginConfig() {
        return this.config;
    }

    /**
     * Get an instance of the {@link BoundConfig}
     *
     * @return Instance of bound config
     */
    public BoundConfig getBoundConfig() {
        return this.boundConfig;
    }

    /**
     * Get an instance of the {@link BeeWorldConfig}
     *
     * @return Instance of BeeWorld config
     * @hidden
     */
    public BeeWorldConfig getBeeWorldConfig() {
        return beeWorldConfig;
    }

    /**
     * Get an instance of the {@link VirtualFurnaceAPI}
     *
     * @return Instance of the Virtual Furnace API
     */
    public VirtualFurnaceAPI getVirtualFurnaceAPI() {
        return virtualFurnaceAPI;
    }

    /**
     * Get an instance of the {@link StructureManager}
     *
     * @return Instance of the Structure Bee Manager
     */
    public StructureManager getStructureManager() {
        return structureManager;
    }

    /**
     * @hidden
     */
    public AddonLoader getAddonLoader() {
        return addonLoader;
    }

}
