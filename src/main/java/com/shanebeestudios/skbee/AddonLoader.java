package com.shanebeestudios.skbee;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Version;
import com.github.goingoffskript.skriptvariabledump.SkriptToYaml;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import com.shanebeestudios.skbee.api.listener.BoundBorderListener;
import com.shanebeestudios.skbee.api.listener.EntityListener;
import com.shanebeestudios.skbee.api.listener.NBTListener;
import com.shanebeestudios.skbee.api.structure.StructureBeeManager;
import com.shanebeestudios.skbee.api.util.LoggerBee;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.bound.config.BoundConfig;
import com.shanebeestudios.skbee.elements.scoreboard.objects.BoardManager;
import com.shanebeestudios.skbee.elements.virtualfurnace.listener.VirtualFurnaceListener;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldConfig;
import com.shanebeestudios.vf.api.VirtualFurnaceAPI;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.io.IOException;

public class AddonLoader {

    private final SkBee plugin;
    private final PluginManager pluginManager;
    private final Config config;
    private final Plugin skriptPlugin;
    private SkriptAddon addon;

    public AddonLoader(SkBee plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
        this.config = plugin.getPluginConfig();
        MinecraftVersion.replaceLogger(LoggerBee.getLogger());
        this.skriptPlugin = pluginManager.getPlugin("Skript");
    }

    boolean canLoadPlugin() {
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
        Version version = new Version(SkBee.EARLIEST_VERSION);
        if (!Skript.isRunningMinecraft(version)) {
            Util.log("&cYour server version &7'&bMC %s&7'&c is not supported, only &7'&bMC %s+&7'&c is supported!", Skript.getMinecraftVersion(), version);
            return false;
        }
        return true;
    }

    void loadSkriptElements() {
        this.addon = Skript.registerAddon(this.plugin);
        this.addon.setLanguageFileDirectory("lang");

        int[] elementCountBefore = SkriptUtils.getElementCount();
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
        loadParticleElements();
        loadTagElements();
        loadRayTraceElements();

        int[] elementCountAfter = SkriptUtils.getElementCount();
        int[] finish = new int[elementCountBefore.length];
        for (int i = 0; i < elementCountBefore.length; i++) {
            finish[i] = elementCountAfter[i] - elementCountBefore[i];
        }
        String[] elementNames = new String[]{"event", "effect", "expression", "condition", "section"};

        Util.log("Loaded elements:");
        for (int i = 0; i < finish.length; i++) {
            Util.log(" - %s %s%s", finish[i], elementNames[i], finish[i] == 1 ? "" : "s");
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
                pluginManager.registerEvents(new NBTListener(), this.plugin);
            }
            Util.logLoading("&5NBT Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadScoreboardElements() {
        if (!this.config.ELEMENTS_BOARD) {
            Util.logLoading("&5Scoreboard Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.scoreboard");
            pluginManager.registerEvents(new BoardManager(), this.plugin);
            Util.logLoading("&5Scoreboard Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadObjectiveElements() {
        if (!this.config.ELEMENTS_OBJECTIVE) {
            Util.logLoading("&5Scoreboard Objective Elements &cdisabled via config");
            return;
        }
        if (Classes.getClassInfoNoError("objective") != null || Classes.getExactClassInfo(Objective.class) != null) {
            Util.logLoading("&5Scoreboard Objective Elements &cdisabled");
            Util.logLoading("&7It appears another Skript addon may have registered Scoreboard Objective syntax.");
            Util.logLoading("&7To use SkBee Scoreboard Objectives, please remove the addon which has registered Scoreboard Objective already.");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.objective");
            Util.logLoading("&5Scoreboard Objective Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadBoundElements() {
        if (!this.config.ELEMENTS_BOUND) {
            Util.logLoading("&5Bound Elements &cdisabled via config");
            return;
        }
        try {
            this.plugin.boundConfig = new BoundConfig(this.plugin);
            pluginManager.registerEvents(new BoundBorderListener(this.plugin), this.plugin);
            addon.loadClasses("com.shanebeestudios.skbee.elements.bound");
            Util.logLoading("&5Bound Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
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

        this.plugin.structureBeeManager = new StructureBeeManager();
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.structure");
            Util.logLoading("&5Structure Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadVirtualFurnaceElements() {
        if (!this.config.ELEMENTS_VIRTUAL_FURNACE) {
            Util.logLoading("&5Virtual Furnace Elements &cdisabled via config");
            return;
        }
        try {
            this.plugin.virtualFurnaceAPI = new VirtualFurnaceAPI(this.plugin, true);
            pluginManager.registerEvents(new VirtualFurnaceListener(), this.plugin);
            addon.loadClasses("com.shanebeestudios.skbee.elements.virtualfurnace");
            Util.logLoading("&5Virtual Furnace Elements &asuccessfully loaded");
        } catch (IOException e) {
            e.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadOtherElements() {
        try {
            pluginManager.registerEvents(new EntityListener(), this.plugin);
            addon.loadClasses("com.shanebeestudios.skbee.elements.other");
            Util.logLoading("&5Other Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadWorldCreatorElements() {
        if (!this.config.ELEMENTS_WORLD_CREATOR) {
            Util.logLoading("&5World Creator Elements &cdisabled via config");
            return;
        }
        try {
            this.plugin.beeWorldConfig = new BeeWorldConfig(this.plugin);
            addon.loadClasses("com.shanebeestudios.skbee.elements.worldcreator");
            Util.logLoading("&5World Creator Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
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
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadParticleElements() {
        if (!this.config.ELEMENTS_PARTICLE) {
            Util.logLoading("&5Particle Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.particle");
            Util.logLoading("&5Particle Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadTagElements() {
        if (!this.config.ELEMENTS_MINECRAFT_TAG) {
            Util.logLoading("&5Minecraft Tag elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.tag");
            Util.logLoading("&5Minecraft Tag elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
        }
    }

    private void loadRayTraceElements() {
        if (!this.config.ELEMENTS_RAYTRACE) {
            Util.logLoading("&5RayTrace elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.raytrace");
            Util.logLoading("&5RayTrace elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pluginManager.disablePlugin(this.plugin);
        }
    }

}
