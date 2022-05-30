package com.shanebeestudios.skbee;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.registrations.Classes;
import com.github.goingoffskript.skriptvariabledump.SkriptToYaml;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import com.shanebeestudios.skbee.api.listener.BoundBorderListener;
import com.shanebeestudios.skbee.api.listener.EntityListener;
import com.shanebeestudios.skbee.api.listener.NBTListener;
import com.shanebeestudios.skbee.api.structure.StructureBeeManager;
import com.shanebeestudios.skbee.api.util.LoggerBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.board.objects.BeeTeams;
import com.shanebeestudios.skbee.elements.bound.config.BoundConfig;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import com.shanebeestudios.skbee.elements.scoreboard.objects.BoardManager;
import com.shanebeestudios.skbee.elements.virtualfurnace.listener.VirtualFurnaceListener;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldConfig;
import com.shanebeestudios.skbee.metrics.Metrics;
import com.shanebeestudios.vf.api.VirtualFurnaceAPI;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**
 * Main class for SkBee
 */
public class SkBee extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(Bound.class, "Bound");
    }

    private static SkBee instance;
    private Plugin skriptPlugin;
    private NBTApi nbtApi;
    private PluginManager pm;
    private Config config;
    private BoundConfig boundConfig = null;
    private SkriptAddon addon;
    private VirtualFurnaceAPI virtualFurnaceAPI;
    private BeeWorldConfig beeWorldConfig;
    private BeeTeams beeTeams = null;// TODO remove after proper testing
    private StructureBeeManager structureBeeManager = null;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        this.config = new Config(this);
        MinecraftVersion.replaceLogger(LoggerBee.getLogger());
        this.pm = Bukkit.getPluginManager();
        PluginDescriptionFile desc = getDescription();

        this.skriptPlugin = pm.getPlugin("Skript");

        if (skriptPlugin == null) {
            Util.log("&cDependency Skript was not found, plugin disabling.");
            pm.disablePlugin(this);
            return;
        }
        if (!skriptPlugin.isEnabled()) {
            Util.log("&cDependency Skript is not enabled, plugin disabling.");
            Util.log("&cThis could mean SkBee is being forced to load before Skript.");
            pm.disablePlugin(this);
            return;
        }
        if (!Skript.isAcceptRegistrations()) {
            // SkBee should be loading right after Skript, during Skript's registration period
            // If a plugin is delaying SkBee's loading, this causes issues with registrations and no longer works
            // We need to find the route of this issue, so far the only plugin I know that does this is FAWE
            Util.log("&cSkript is no longer accepting registrations.");
            Util.log("&cNo clue how this could happen.");
            Util.log("&cSeems a plugin is delaying SkBee loading, which is after Skript stops accepting registrations.");
            pm.disablePlugin(this);
            return;
        }
        if (!Skript.isRunningMinecraft(1, 17, 1)) {
            Util.log("&cYour server version &7'&b%s&7'&c is not supported, only MC 1.17.1+ is supported!", Skript.getMinecraftVersion());
            pm.disablePlugin(this);
            return;
        }

        addon = Skript.registerAddon(this);
        addon.setLanguageFileDirectory("lang");
        this.nbtApi = new NBTApi();

        // Load Skript elements
        loadNBTElements();
        loadRecipeElements();
        //loadBoardElements(); TODO remove after proper testing
        loadScoreboardElements();
        loadBoundElements();
        loadTextElements();
        loadPathElements();
        loadStructureElements();
        loadOtherElements();
        loadVirtualFurnaceElements();
        loadWorldCreatorElements();
        loadGameEventElements();
        loadBossBarElements();

        // Beta check + notice
        if (desc.getVersion().contains("Beta")) {
            Util.log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
            Util.log("&ehttps://github.com/ShaneBeee/SkBee/issues");
        }

        loadMetrics();
        Util.log("&aSuccessfully enabled v%s&7 in &b%.2f seconds", desc.getVersion(), (float) (System.currentTimeMillis() - start) / 1000);

        if (this.beeWorldConfig != null && this.config.AUTO_LOAD_WORLDS) {
            this.beeWorldConfig.loadCustomWorlds();
        }
    }

    private void loadNBTElements() {
        if (!this.config.ELEMENTS_NBT) {
            Util.log("&5NBT Elements &cdisabled via config");
            return;
        }
        if (!this.nbtApi.isEnabled()) {
            String ver = Skript.getMinecraftVersion().toString();
            Util.log("&5NBT Elements &cDISABLED!");
            Util.log(" - Your server version [&b" + ver + "&7] is not currently supported by the NBT-API");
            Util.log(" - This is not a bug!");
            Util.log(" - NBT elements will resume once the API is updated to work with [&b" + ver + "&7]");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.nbt");
            // Allow for serializing NBT compounds via 'skript-variable-dump'
            Plugin plugin = Bukkit.getPluginManager().getPlugin("skript-variable-dump");
            if (plugin != null && Skript.classExists("com.github.goingoffskript.skriptvariabledump.SkriptToYaml")) {
                SkriptToYaml.adapts(NBTContainer.class, (compound, map) -> map.put("nbt-compound", compound.toString()));
            }
            if (NBTApi.SUPPORTS_BLOCK_NBT) {
                pm.registerEvents(new NBTListener(), this);
            }
            Util.log("&5NBT Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadRecipeElements() {
        if (!this.config.ELEMENTS_RECIPE) {
            Util.log("&5Recipe Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.recipe");
            Util.log("&5Recipe Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

//    private void loadBoardElements() { TODO remove after proper testing
//        if (!this.config.ELEMENTS_BOARD) {
//            Util.log("&5Scoreboard Elements &cdisabled via config");
//            return;
//        }
//        try {
//            //beeTeams = new BeeTeams();
//            addon.loadClasses("com.shanebeestudios.skbee.elements.board");
//            pm.registerEvents(new PlayerBoardListener(this), this);
//            // If there are players online during a reload, let's give them a board
//            Bukkit.getOnlinePlayers().forEach(Board::createBoard);
//            Util.log("&5Scoreboard Elements &asuccessfully loaded");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            pm.disablePlugin(this);
//        }
//    }

    private void loadScoreboardElements() {
        if (!this.config.ELEMENTS_BOARD) {
            Util.log("&5Scoreboard Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.scoreboard");
            pm.registerEvents(new BoardManager(), this);
            Util.log("&5Scoreboard Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadBoundElements() {
        if (!this.config.ELEMENTS_BOUND) {
            Util.log("&5Bound Elements &cdisabled via config");
            return;
        }
        try {
            this.boundConfig = new BoundConfig(this);
            pm.registerEvents(new BoundBorderListener(this), this);
            addon.loadClasses("com.shanebeestudios.skbee.elements.bound");
            Util.log("&5Bound Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadTextElements() {
        if (!this.config.ELEMENTS_TEXT_COMPONENT) {
            Util.log("&5Text Component Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.text");
            Util.log("&5Text Component Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadPathElements() {
        if (!this.config.ELEMENTS_PATHFINDING) {
            Util.log("&5Pathfinding Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.path");
            Util.log("&5Pathfinding Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadStructureElements() {
        if (!this.config.ELEMENTS_STRUCTURE) {
            Util.log("&5Structure Elements &cdisabled via config");
            return;
        }

        this.structureBeeManager = new StructureBeeManager();
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.structure");
            Util.log("&5New Structure Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadVirtualFurnaceElements() {
        if (Skript.classExists("org.bukkit.persistence.PersistentDataContainer")) {
            if (!this.config.ELEMENTS_VIRTUAL_FURNACE) {
                Util.log("&5Virtual Furnace Elements &cdisabled via config");
                return;
            }
            try {
                this.virtualFurnaceAPI = new VirtualFurnaceAPI(this, true);
                pm.registerEvents(new VirtualFurnaceListener(), this);
                addon.loadClasses("com.shanebeestudios.skbee.elements.virtualfurnace");
                Util.log("&5Virtual Furnace Elements &asuccessfully loaded");
            } catch (IOException e) {
                e.printStackTrace();
                pm.disablePlugin(this);
            }
        } else {
            Util.log("&5Virtual Furnace Elements &cdisabled");
            Util.log("&7 - Virtual Furnace elements are only available on 1.14+");
        }
    }

    private void loadOtherElements() {
        try {
            pm.registerEvents(new EntityListener(), this);
            addon.loadClasses("com.shanebeestudios.skbee.elements.other");
            Util.log("&5Other Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadWorldCreatorElements() {
        if (!this.config.ELEMENTS_WORLD_CREATOR) {
            Util.log("&5World Creator Elements &cdisabled via config");
            return;
        }
        try {
            this.beeWorldConfig = new BeeWorldConfig(this);
            addon.loadClasses("com.shanebeestudios.skbee.elements.worldcreator");
            Util.log("&5World Creator Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadGameEventElements() {
        if (!this.config.ELEMENTS_GAME_EVENT) {
            Util.log("&5Game Event Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.gameevent");
            Util.log("&5Game Event Elements &asuccessfully loaded");
        } catch (IOException e) {
            e.printStackTrace();
            pm.disablePlugin(this);
        }

    }

    private void loadBossBarElements() {
        if (!this.config.ELEMENTS_BOSS_BAR) {
            Util.log("&5BossBar Elements &cdisabled via config");
            return;
        }
        if (Classes.getClassInfoNoError("bossbar") != null || Classes.getExactClassInfo(BossBar.class) != null) {
            Util.log("&5BossBar Elements &cdisabled");
            Util.log("&7It appears another Skript addon may have registered BossBar syntax.");
            Util.log("&7To use SkBee BossBars, please remove the addon which has registered BossBars already.");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.bossbar");
            Util.log("&5BossBar Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }

    }

    private void loadMetrics() { //6719
        Metrics metrics = new Metrics(this, 6719);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));
    }

    @Override
    public void onDisable() {
        if (this.virtualFurnaceAPI != null) {
            this.virtualFurnaceAPI.disableAPI();
        }

//        if (this.config.ELEMENTS_BOARD) { TODO remove after proper testing
//            Board.clearBoards();
//        }
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

    // TODO notes
    public BeeWorldConfig getBeeWorldConfig() {
        return beeWorldConfig;
    }

    /**
     * Get an instance of the {@link NBTApi}
     *
     * @return Instance of the NBT API
     */
    public NBTApi getNbtApi() {
        return nbtApi;
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

    public BeeTeams getBeeTeams() { //TODO remove after proper testing
        return beeTeams;
    }

}
