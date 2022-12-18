package com.shanebeestudios.skbee;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Version;
import com.github.goingoffskript.skriptvariabledump.SkriptToYaml;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import com.shanebeestudios.skbee.api.command.SkBeeInfo;
import com.shanebeestudios.skbee.api.listener.BoundBorderListener;
import com.shanebeestudios.skbee.api.listener.EntityListener;
import com.shanebeestudios.skbee.api.listener.NBTListener;
import com.shanebeestudios.skbee.api.listener.ScriptListener;
import com.shanebeestudios.skbee.api.structure.StructureBeeManager;
import com.shanebeestudios.skbee.api.util.LoggerBee;
import com.shanebeestudios.skbee.api.util.UpdateChecker;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.bound.config.BoundConfig;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import com.shanebeestudios.skbee.elements.scoreboard.objects.BoardManager;
import com.shanebeestudios.skbee.elements.virtualfurnace.listener.VirtualFurnaceListener;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldConfig;
import com.shanebeestudios.vf.api.VirtualFurnaceAPI;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.io.IOException;

/**
 * Main class for SkBee
 */
public class SkBee extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(Bound.class, "Bound");
    }

    private static final int[] EARLIEST_VERSION = new int[]{1, 17, 1};

    private static SkBee instance;
    private Plugin skriptPlugin;
    private PluginManager pm;
    private Config config;
    private BoundConfig boundConfig = null;
    private SkriptAddon addon;
    private VirtualFurnaceAPI virtualFurnaceAPI;
    private BeeWorldConfig beeWorldConfig;
    private StructureBeeManager structureBeeManager = null;

    @Override
    public void onEnable() {
        // Let's get this party started...
        long start = System.currentTimeMillis();
        instance = this;
        this.config = new Config(this);
        MinecraftVersion.replaceLogger(LoggerBee.getLogger());
        this.pm = Bukkit.getPluginManager();
        this.skriptPlugin = pm.getPlugin("Skript");

        // Check if SkriptAddon can actually load
        if (!canLoadPlugin()) {
            pm.disablePlugin(this);
            return;
        }

        loadSkriptElements();
        loadCommands();
        loadMetrics();

        // Beta check + notice
        String version = getDescription().getVersion();
        if (version.contains("-")) {
            Util.log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
            Util.log("&ehttps://github.com/ShaneBeee/SkBee/issues");
        }

        checkUpdate(version);
        Util.log("&aSuccessfully enabled v%s&7 in &b%.2f seconds", version, (float) (System.currentTimeMillis() - start) / 1000);

        // Load custom worlds if enabled in config
        if (this.beeWorldConfig != null && this.config.AUTO_LOAD_WORLDS) {
            this.beeWorldConfig.loadCustomWorlds();
        }
        // Looks like we made it after all
    }

    private boolean canLoadPlugin() {
        if (skriptPlugin == null) {
            Util.log("&cDependency Skript was not found, plugin disabling.");
            return false;
        }
        if (!skriptPlugin.isEnabled()) {
            Util.log("&cDependency Skript is not enabled, plugin disabling.");
            Util.log("&cThis could mean SkBee is being forced to load before Skript.");
            return false;
        }
        if (!Skript.isAcceptRegistrations()) {
            // SkBee should be loading right after Skript, during Skript's registration period
            // If a plugin is delaying SkBee's loading, this causes issues with registrations and no longer works
            // We need to find the route of this issue, so far the only plugin I know that does this is FAWE
            Util.log("&cSkript is no longer accepting registrations.");
            Util.log("&cNo clue how this could happen.");
            Util.log("&cSeems a plugin is delaying SkBee loading, which is after Skript stops accepting registrations.");
            return false;
        }
        Version version = new Version(EARLIEST_VERSION);
        if (!Skript.isRunningMinecraft(version)) {
            Util.log("&cYour server version &7'&bMC %s&7'&c is not supported, only &7'&bMC %s+&7'&c is supported!", Skript.getMinecraftVersion(), version);
            return false;
        }
        return true;
    }

    private void loadSkriptElements() {
        addon = Skript.registerAddon(this);
        addon.setLanguageFileDirectory("lang");

        loadNBTElements();
        loadRecipeElements();
        loadScoreboardElements();
        loadObjectiveElements();
        loadTeamElements();
        loadBoundElements();
        loadTextElements();
        loadPathElements();
        loadStructureElements();
        loadOtherElements();
        loadVirtualFurnaceElements();
        loadWorldCreatorElements();
        loadGameEventElements();
        loadBossBarElements();
        loadStatisticElements();
        loadVillagerElements();
        loadAdvancementElements();
        loadWorldBorderElements();
    }

    private void loadCommands() {
        //noinspection ConstantConditions
        getCommand("skbee").setExecutor(new SkBeeInfo(this));
        pm.registerEvents(new UpdateChecker(this), this);
        pm.registerEvents(new ScriptListener(), this);
    }

    private void checkUpdate(String version) {
        if (config.SETTINGS_UPDATE_CHECKER) {
            UpdateChecker.checkForUpdate(version);
        } else {
            Util.log("Update checker disabled... will not check for update!");
        }
    }

    private void loadNBTElements() {
        NBTApi.initializeAPI();
        if (!this.config.ELEMENTS_NBT) {
            Util.logLoading("&5NBT Elements &cdisabled via config");
            return;
        }
        if (!NBTApi.isEnabled()) {
            String ver = Skript.getMinecraftVersion().toString();
            Util.logLoading("&5NBT Elements &cDISABLED!");
            Util.logLoading(" - Your server version [&b" + ver + "&7] is not currently supported by the NBT-API");
            Util.logLoading(" - This is not a bug!");
            Util.logLoading(" - NBT elements will resume once the API is updated to work with [&b" + ver + "&7]");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.nbt");
            // Allow for serializing NBT compounds via 'skript-variable-dump'
            Plugin plugin = Bukkit.getPluginManager().getPlugin("skript-variable-dump");
            if (plugin != null && Skript.classExists("com.github.goingoffskript.skriptvariabledump.SkriptToYaml")) {
                SkriptToYaml.adapts(NBTContainer.class, (compound, map) -> map.put("nbt-compound", compound.toString()));
            }
            if (NBTApi.supportsBlockNBT()) {
                pm.registerEvents(new NBTListener(), this);
            }
            Util.logLoading("&5NBT Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadRecipeElements() {
        if (!this.config.ELEMENTS_RECIPE) {
            Util.logLoading("&5Recipe Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.recipe");
            Util.logLoading("&5Recipe Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadScoreboardElements() {
        if (!this.config.ELEMENTS_BOARD) {
            Util.logLoading("&5Scoreboard Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.scoreboard");
            pm.registerEvents(new BoardManager(), this);
            Util.logLoading("&5Scoreboard Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadObjectiveElements() {
        if (!this.config.ELEMENTS_OBJECTIVE) {
            Util.logLoading("&5Scoreboard Objective Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.objective");
            Util.logLoading("&5Scoreboard Objective Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadTeamElements() {
        if (!this.config.ELEMENTS_TEAM) {
            Util.logLoading("&5Team Elements &cdisabled via config");
            return;
        }
        if (Classes.getClassInfoNoError("team") != null || Classes.getExactClassInfo(Team.class) != null) {
            Util.logLoading("&5Team Elements &cdisabled");
            Util.logLoading("&7It appears another Skript addon may have registered Team syntax.");
            Util.logLoading("&7To use SkBee Teams, please remove the addon which has registered Teams already.");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.team");
            Util.logLoading("&5Team Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadBoundElements() {
        if (!this.config.ELEMENTS_BOUND) {
            Util.logLoading("&5Bound Elements &cdisabled via config");
            return;
        }
        try {
            this.boundConfig = new BoundConfig(this);
            pm.registerEvents(new BoundBorderListener(this), this);
            addon.loadClasses("com.shanebeestudios.skbee.elements.bound");
            Util.logLoading("&5Bound Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadTextElements() {
        if (!this.config.ELEMENTS_TEXT_COMPONENT) {
            Util.logLoading("&5Text Component Elements &cdisabled via config");
            return;
        }
        if (!Skript.classExists("net.kyori.adventure.text.Component")) {
            Util.logLoading("&5Text Component Elements &cdisabled");
            Util.logLoading("&7- Text components require a PaperMC server.");
            return;
        }
        if (Classes.getClassInfoNoError("textcomponent") != null) {
            Util.logLoading("&5Text Component Elements &cdisabled");
            Util.logLoading("&7It appears another Skript addon may have registered Text Component syntax.");
            Util.logLoading("&7To use SkBee Text Components, please remove the addon which has registered Text Components already.");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.text");
            Util.logLoading("&5Text Component Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadPathElements() {
        if (!this.config.ELEMENTS_PATHFINDING) {
            Util.logLoading("&5Pathfinding Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.path");
            Util.logLoading("&5Pathfinding Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadStructureElements() {
        if (!this.config.ELEMENTS_STRUCTURE) {
            Util.logLoading("&5Structure Elements &cdisabled via config");
            return;
        }

        // This was added in Oct/2021 (so just before 1.18 came out)
        if (!Skript.methodExists(Bukkit.class, "getStructureManager")) {
            Util.logLoading("&cIt appears structure elements are not available on your server version.");
            Util.logLoading("&5Structure Elements &cdisabled");
            return;
        }

        this.structureBeeManager = new StructureBeeManager();
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.structure");
            Util.logLoading("&5Structure Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadVirtualFurnaceElements() {
        if (!this.config.ELEMENTS_VIRTUAL_FURNACE) {
            Util.logLoading("&5Virtual Furnace Elements &cdisabled via config");
            return;
        }
        try {
            this.virtualFurnaceAPI = new VirtualFurnaceAPI(this, true);
            pm.registerEvents(new VirtualFurnaceListener(), this);
            addon.loadClasses("com.shanebeestudios.skbee.elements.virtualfurnace");
            Util.logLoading("&5Virtual Furnace Elements &asuccessfully loaded");
        } catch (IOException e) {
            e.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadOtherElements() {
        try {
            pm.registerEvents(new EntityListener(), this);
            addon.loadClasses("com.shanebeestudios.skbee.elements.other");
            Util.logLoading("&5Other Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadWorldCreatorElements() {
        if (!this.config.ELEMENTS_WORLD_CREATOR) {
            Util.logLoading("&5World Creator Elements &cdisabled via config");
            return;
        }
        try {
            this.beeWorldConfig = new BeeWorldConfig(this);
            addon.loadClasses("com.shanebeestudios.skbee.elements.worldcreator");
            Util.logLoading("&5World Creator Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadGameEventElements() {
        if (!this.config.ELEMENTS_GAME_EVENT) {
            Util.logLoading("&5Game Event Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.gameevent");
            Util.logLoading("&5Game Event Elements &asuccessfully loaded");
        } catch (IOException e) {
            e.printStackTrace();
            pm.disablePlugin(this);
        }

    }

    private void loadBossBarElements() {
        if (!this.config.ELEMENTS_BOSS_BAR) {
            Util.logLoading("&5BossBar Elements &cdisabled via config");
            return;
        }
        if (Classes.getClassInfoNoError("bossbar") != null || Classes.getExactClassInfo(BossBar.class) != null) {
            Util.logLoading("&5BossBar Elements &cdisabled");
            Util.logLoading("&7It appears another Skript addon may have registered BossBar syntax.");
            Util.logLoading("&7To use SkBee BossBars, please remove the addon which has registered BossBars already.");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.bossbar");
            Util.logLoading("&5BossBar Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }

    }

    private void loadStatisticElements() {
        if (!this.config.ELEMENTS_STATISTIC) {
            Util.logLoading("&5Statistic Elements &cdisabled via config");
            return;
        }
        if (Classes.getClassInfoNoError("statistic") != null || Classes.getExactClassInfo(Statistic.class) != null) {
            Util.logLoading("&5Statistic Elements &cdisabled");
            Util.logLoading("&7It appears another Skript addon may have registered Statistic syntax.");
            Util.logLoading("&7To use SkBee Statistics, please remove the addon which has registered Statistic already.");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.statistic");
            Util.logLoading("&5Statistic Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadVillagerElements() {
        if (!this.config.ELEMENTS_VILLAGER) {
            Util.logLoading("&5Villager Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.villager");
            Util.logLoading("&5Villager Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadAdvancementElements() {
        if (!this.config.ELEMENTS_ADVANCEMENT) {
            Util.logLoading("&5Advancement Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.advancement");
            Util.logLoading("&5Advancement Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadWorldBorderElements() {
        if (!this.config.ELEMENTS_WORLD_BORDER) {
            Util.logLoading("&5World Border Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.worldborder");
            Util.logLoading("&5World Border Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadMetrics() { //6719
        Metrics metrics = new Metrics(this, 6719);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
    }

    @Override
    public void onDisable() {
        if (this.virtualFurnaceAPI != null) {
            this.virtualFurnaceAPI.disableAPI();
        }
        if (this.boundConfig != null) {
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

    public Plugin getSkriptPlugin() {
        return this.skriptPlugin;
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

    public StructureBeeManager getStructureBeeManager() {
        return structureBeeManager;
    }

}
