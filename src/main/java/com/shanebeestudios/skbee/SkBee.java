package com.shanebeestudios.skbee;

import ch.njol.skript.Skript;
import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.util.Version;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.command.SkBeeInfo;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.util.update.UpdateChecker;
import com.shanebeestudios.skbee.api.wrapper.LazyLocation;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldConfig;
import com.shanebeestudios.vf.api.VirtualFurnaceAPI;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.permissions.DefaultPermissions;

/**
 * Main class for SkBee
 */
public class SkBee extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(Bound.class, "Bound");
        ConfigurationSerialization.registerClass(LazyLocation.class, "LazyLocation");
    }

    private static SkBee instance;
    private Version skBeeVersion;
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
    @Override
    public void onEnable() {
        // Let's get this party started...
        long start = System.currentTimeMillis();
        instance = this;
        this.skBeeVersion = new Version(this.getPluginMeta().getVersion());
        this.config = new Config(this);
        TaskUtils.initialize(this, Util.IS_RUNNING_FOLIA || this.config.settings_use_paper_schedulers);


        this.addonLoader = new AddonLoader(this);
        // Check if SkriptAddon can actually load
        this.properlyEnabled = addonLoader.canLoadPlugin();

        loadCommands();
        loadMetrics();

        // Beta check + notice
        if (this.skBeeVersion.toString().contains("-")) {
            Util.log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
            Util.log("&ehttps://github.com/ShaneBeee/SkBee/issues");
        }

        new UpdateChecker(this);
        Util.log("&aSuccessfully enabled v%s&7 in &b%.2f seconds", this.skBeeVersion.toString(), (float) (System.currentTimeMillis() - start) / 1000);

        // Load custom worlds if enabled in config
        if (this.properlyEnabled && this.beeWorldConfig != null) {
            this.beeWorldConfig.loadCustomWorlds();
        }
        // Looks like we made it after all
    }

    private void loadCommands() {
        registerCommand("skbee", new SkBeeInfo(this));
        //pm.registerEvents(new ScriptListener(), this); // Temp removed
        DefaultPermissions.registerPermission("skbee.admin", "Permission to receive error messages", PermissionDefault.OP);
        DefaultPermissions.registerPermission("skbee.command", "Permission to use SkBee's main command", PermissionDefault.OP);
    }

    private void loadMetrics() { //6719
        Metrics metrics = new Metrics(this, 6719);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
        metrics.addCustomChart(new SimplePie("virtual_furnace", () -> String.valueOf(config.ELEMENTS_VIRTUAL_FURNACE)));

        // New Metrics
        // Many of these are copied from Skript -> SkriptMetrics.class
        metrics.addCustomChart(new DrilldownPie("plugin_version_drilldown_pie", () -> {
            Version version = new Version(this.getPluginMeta().getVersion());
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);
            table.put(
                version.getMajor() + "." + version.getMinor(), // upper label
                version.toString(), // lower label
                1 // weight
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("skript_version_drilldown_pie", () -> {
            Version version = Skript.getVersion();
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);
            table.put(
                version.getMajor() + "." + version.getMinor(), // upper label
                version.toString(), // lower label
                1 // weight
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("minecraft_version_drilldown_pie", () -> {
            Version version = Skript.getMinecraftVersion();
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);

            if (version.getMajor() == 1) {
                // Minecraft 1.x.x versioning
                table.put(
                    version.getMajor() + "." + version.getMinor(), // upper label
                    version.toString(), // lower label
                    1 // weight
                );
            } else {
                // Minecraft (year).x.x versioning
                table.put(
                    "" + version.getMajor(), // upper label
                    version.toString(), // lower label
                    1 // weight
                );
            }
            return table.rowMap();
        }));
        // Monitor Skript/Minecraft versions used per release of SkBee
        // This helps us understand which versions of Skript and Minecraft are most commonly used with SkBee per release
        metrics.addCustomChart(new DrilldownPie("skript_version_per_release_drilldown_pie", () -> {
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);
            Version skriptVersion = Skript.getVersion();

            table.put(
                this.skBeeVersion.getMajor() + "." + this.skBeeVersion.getMinor() + ".x",
                skriptVersion.toString(),
                1
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("minecraft_version_per_release_drilldown_pie", () -> {
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);
            Version minecraftVersion = Skript.getMinecraftVersion();

            table.put(
                this.skBeeVersion.getMajor() + "." + this.skBeeVersion.getMinor() + ".x",
                minecraftVersion.toString(),
                1
            );
            return table.rowMap();
        }));
    }

    /**
     * @hidden
     */
    @Override
    public void onDisable() {
        if (this.properlyEnabled) {
            // Cancel tasks on stop to prevent async issues
            TaskUtils.cancelTasks();
        }
        if (this.virtualFurnaceAPI != null) {
            this.virtualFurnaceAPI.disableAPI();
        }
        // Only save bounds if not in test mode
        if (!TestMode.ENABLED && this.boundConfig != null) {
            this.boundConfig.saveAllBoundsOnShutdown();
        }
        // Clear debugs in case of some kind of reload
        Util.clearDebugs();
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

    /**
     * Check if SkBee's debugger is enabled
     *
     * @return True if debugger enabled
     */
    public static boolean isDebug() {
        Config config = SkBee.instance.config;
        if (config != null) {
            return config.settings_debug;
        }
        return false;
    }

}
