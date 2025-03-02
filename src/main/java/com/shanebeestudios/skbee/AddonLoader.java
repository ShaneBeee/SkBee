package com.shanebeestudios.skbee;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.util.Version;
import com.shanebeestudios.skbee.api.listener.EntityListener;
import com.shanebeestudios.skbee.api.listener.NBTListener;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.fastboard.FastBoardManager;
import com.shanebeestudios.skbee.api.property.PropertyRegistry;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.util.LoggerBee;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.virtualfurnace.listener.VirtualFurnaceListener;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldConfig;
import com.shanebeestudios.vf.api.VirtualFurnaceAPI;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Statistic;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Objective;

import java.io.IOException;

/**
 * @hidden
 */
public class AddonLoader {

    private final SkBee plugin;
    private final PluginManager pluginManager;
    private final Config config;
    private final Plugin skriptPlugin;
    private SkriptAddon addon;
    private boolean textComponentEnabled;

    public AddonLoader(SkBee plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
        this.config = plugin.getPluginConfig();
        MinecraftVersion.replaceLogger(LoggerBee.getLogger());
        this.skriptPlugin = pluginManager.getPlugin("Skript");
    }

    boolean canLoadPlugin() {
        if (skriptPlugin == null) {
            Util.logLoading("&cDependency Skript was not found, Skript elements cannot load.");
            return false;
        }
        if (!skriptPlugin.isEnabled()) {
            Util.logLoading("&cDependency Skript is not enabled, Skript elements cannot load.");
            Util.logLoading("&cThis could mean SkBee is being forced to load before Skript.");
            return false;
        }
        Version skriptVersion = Skript.getVersion();
        if (skriptVersion.isSmallerThan(new Version(2, 9, 999))) {
            Util.logLoading("&cDependency Skript outdated, Skript elements cannot load.");
            Util.logLoading("&eSkBee requires Skript 2.10+ but found Skript " + skriptVersion);
            return false;
        }
        if (!Skript.isAcceptRegistrations()) {
            // SkBee should be loading right after Skript, during Skript's registration period
            // If a plugin is delaying SkBee's loading, this causes issues with registrations and no longer works
            // We need to find the route of this issue, so far the only plugin I know that does this is PlugMan
            Util.logLoading("&cSkript is no longer accepting registrations, addons can no longer be loaded!");
            if (isPlugmanReloaded()) {
                Util.logLoading("&cIt appears you're running PlugMan.");
                Util.logLoading("&cIf you're trying to reload/enable SkBee with PlugMan.... you can't.");
                Util.logLoading("&ePlease restart your server!");
            } else {
                Util.logLoading("&cNo clue how this could happen.");
                Util.logLoading("&cSeems a plugin is delaying SkBee loading, which is after Skript stops accepting registrations.");
            }
            return false;
        }
        Version version = new Version(SkBee.EARLIEST_VERSION);
        if (!Skript.isRunningMinecraft(version)) {
            Util.logLoading("&cYour server version &7'&bMC %s&7'&c is not supported, only &7'&bMC %s+&7'&c is supported!", Skript.getMinecraftVersion(), version);
            Util.logLoading("&7For outdated server versions please see: &ehttps://github.com/ShaneBeee/SkBee#outdated");
            return false;
        }
        loadSkriptElements();
        return true;
    }

    private void loadSkriptElements() {
        this.addon = Skript.registerAddon(this.plugin);
        this.addon.setLanguageFileDirectory("lang");

        int[] elementCountBefore = SkriptUtils.getElementCount();
        // Load first as these are the base for many things
        loadRegistryElements();
        loadOtherElements();
        loadNBTElements();
        loadTextElements();

        // Load in alphabetical order (to make "/skbee info" easier to read)
        loadAdvancementElements();
        loadBossBarElements();
        loadBoundElements();
        loadDamageSourceElements();
        loadDisplayEntityElements();
        loadFastboardElements();
        loadFishingElements();
        loadGameEventElements();
        loadItemComponentElements();
        loadParticleElements();
        loadPropertyElements();
        loadRayTraceElements();
        loadRecipeElements();
        loadScoreboardElements();
        loadStatisticElements();
        loadStructureElements();
        loadSwitchCaseElements();
        loadTagElements();
        loadTickManagerElements();
        loadVillagerElements();
        loadVirtualFurnaceElements();
        loadWorldBorderElements();
        loadWorldCreatorElements();
        loadChunkGenElements();
        loadTestingElements();

        int[] elementCountAfter = SkriptUtils.getElementCount();
        int[] finish = new int[elementCountBefore.length];
        int total = 0;
        for (int i = 0; i < elementCountBefore.length; i++) {
            finish[i] = elementCountAfter[i] - elementCountBefore[i];
            total += finish[i];
        }
        String[] elementNames = new String[]{"event", "effect", "expression", "condition", "section"};

        Util.log("Loaded (%s) elements:", total);
        for (int i = 0; i < finish.length; i++) {
            Util.log(" - %s %s%s", finish[i], elementNames[i], finish[i] == 1 ? "" : "s");
        }
        if (this.config.ELEMENTS_PROPERTY) {
            int size = PropertyRegistry.properties().size();
            Util.log(" - %s properties",size);
        }
        if (this.config.RUNTIME_DISABLE_ERRORS) {
            Util.logLoading("&eRuntime Errors have been disabled via config!");
        }
        if (this.config.RUNTIME_DISABLE_WARNINGS) {
            Util.logLoading("&eRuntime Warnings have been disabled via config!");
        }
    }

    private void loadNBTElements() {
        if (!this.config.ELEMENTS_NBT) {
            Util.logLoading("&5NBT Elements &cdisabled via config");
            return;
        }
        NBTApi.initializeAPI();
        if (!NBTApi.isEnabled()) {
            String ver = Skript.getMinecraftVersion().toString();
            Util.logLoading("&5NBT Elements &cDISABLED!");
            Util.logLoading(" - Your server version [&b" + ver + "&7] is not currently supported by the NBT-API");
            Util.logLoading(" - This is not a bug!");
            Util.logLoading(" - NBT elements will resume once the API is updated to work with [&b" + ver + "&7]");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.nbt");
            new NBTListener(this.plugin);
            Util.logLoading("&5NBT Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("NBT", ex);
        }
    }

    private void loadRecipeElements() {
        if (!this.config.ELEMENTS_RECIPE) {
            Util.logLoading("&5Recipe Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.recipe");
            Util.logLoading("&5Recipe Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Recipe", ex);
        }
    }

    private void loadFastboardElements() {
        if (!this.config.ELEMENTS_FASTBOARD) {
            Util.logLoading("&5Fastboard Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.fastboard");
            pluginManager.registerEvents(new FastBoardManager(), this.plugin);
            String type = FastBoardManager.HAS_ADVENTURE ? "Adventure" : "Legacy";
            Util.logLoading("&5Fastboard&7[&b%s&7] &5Elements &asuccessfully loaded", type);
        } catch (Exception ex) {
            logFailure("Fastboard", ex);
        }
    }

    private void loadScoreboardElements() {
        if (!this.config.ELEMENTS_SCOREBOARD) {
            Util.logLoading("&5Scoreboard Elements &cdisabled via config");
            return;
        }
        if (Classes.getClassInfoNoError("objective") != null || Classes.getExactClassInfo(Objective.class) != null) {
            Util.logLoading("&5Scoreboard Elements &cdisabled");
            Util.logLoading("&7It appears another Skript addon may have registered Scoreboard syntax.");
            Util.logLoading("&7To use SkBee Scoreboards, please remove the addon which has registered Scoreboard already.");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.scoreboard");
            Util.logLoading("&5Scoreboard Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Scoreboard", ex);
        }
    }

    private void loadTickManagerElements() {
        if (!this.config.ELEMENTS_TICK_MANAGER) {
            Util.logLoading("&5Tick Manager Elements &cdisabled via config");
            return;
        }
        if (!Skript.classExists("org.bukkit.ServerTickManager")) {
            Util.logLoading("&5Tick Manager Elements &cdisabled &7(&eRequires Minecraft 1.20.4+&7)");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.tickmanager");
            Util.logLoading("&5Tick Manager Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Tick Manager", ex);
        }
    }

    private void loadBoundElements() {
        if (!this.config.ELEMENTS_BOUND) {
            Util.logLoading("&5Bound Elements &cdisabled via config");
            return;
        }
        try {
            this.plugin.boundConfig = new BoundConfig(this.plugin);
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.bound");
            Util.logLoading("&5Bound Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Bound", ex);
        }
    }

    private void loadTextElements() {
        if (!this.config.ELEMENTS_TEXT_COMPONENT) {
            Util.logLoading("&5Text Component Elements &cdisabled via config");
            return;
        }
        if (!Skript.classExists("io.papermc.paper.event.player.AsyncChatEvent")) {
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
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.text");
            Util.logLoading("&5Text Component Elements &asuccessfully loaded");
            this.textComponentEnabled = true;
        } catch (Exception ex) {
            logFailure("Text Component", ex);
        }
    }

    private void loadStructureElements() {
        if (!this.config.ELEMENTS_STRUCTURE) {
            Util.logLoading("&5Structure Elements &cdisabled via config");
            return;
        }

        this.plugin.structureManager = new StructureManager();
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.structure");
            Util.logLoading("&5Structure Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Structure", ex);
        }
    }

    private void loadVirtualFurnaceElements() {
        if (!this.config.ELEMENTS_VIRTUAL_FURNACE) {
            Util.logLoading("&5Virtual Furnace Elements &cdisabled via config");
            return;
        }
        // PaperMC check
        if (!Skript.classExists("net.kyori.adventure.text.Component")) {
            Util.logLoading("&5Virtual Furnace Elements &cdisabled");
            Util.logLoading("&7- Virtual Furnace require a PaperMC server.");
            return;
        }
        try {
            this.plugin.virtualFurnaceAPI = new VirtualFurnaceAPI(this.plugin, true);
            pluginManager.registerEvents(new VirtualFurnaceListener(), this.plugin);
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.virtualfurnace");
            Util.logLoading("&5Virtual Furnace Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Virtual Furnace", ex);
        }
    }

    private void loadOtherElements() {
        try {
            pluginManager.registerEvents(new EntityListener(), this.plugin);
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.other");
        } catch (Exception ex) {
            logFailure("Other", ex);
        }
    }

    private void loadWorldCreatorElements() {
        if (!this.config.ELEMENTS_WORLD_CREATOR) {
            Util.logLoading("&5World Creator Elements &cdisabled via config");
            return;
        }
        try {
            this.plugin.beeWorldConfig = new BeeWorldConfig(this.plugin);
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.worldcreator");
            Util.logLoading("&5World Creator Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("World Creator", ex);
        }
    }

    private void loadChunkGenElements() {
        if (!this.config.ELEMENTS_CHUNK_GEN) {
            Util.logLoading("&5Chunk Generator Elements &cdisabled via config");
            return;
        }
        if (!this.config.ELEMENTS_WORLD_CREATOR) {
            Util.logLoading("&5Chunk Generator &cdisabled via World Creator config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.generator");
            Util.logLoading("&5Chunk Generator Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Chunk Generator", ex);
        }
    }

    private void loadGameEventElements() {
        if (!this.config.ELEMENTS_GAME_EVENT) {
            Util.logLoading("&5Game Event Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.gameevent");
            Util.logLoading("&5Game Event Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Game Event", ex);
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
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.bossbar");
            Util.logLoading("&5BossBar Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("BossBar", ex);
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
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.statistic");
            Util.logLoading("&5Statistic Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Statistic", ex);
        }
    }

    private void loadVillagerElements() {
        if (!this.config.ELEMENTS_VILLAGER) {
            Util.logLoading("&5Villager Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.villager");
            Util.logLoading("&5Villager Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Villager", ex);
        }
    }

    private void loadAdvancementElements() {
        if (!this.config.ELEMENTS_ADVANCEMENT) {
            Util.logLoading("&5Advancement Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.advancement");
            Util.logLoading("&5Advancement Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Advancement", ex);
        }
    }

    private void loadWorldBorderElements() {
        if (!this.config.ELEMENTS_WORLD_BORDER) {
            Util.logLoading("&5World Border Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.worldborder");
            Util.logLoading("&5World Border Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("World Border", ex);
        }
    }

    private void loadParticleElements() {
        if (!this.config.ELEMENTS_PARTICLE) {
            Util.logLoading("&5Particle Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.particle");
            Util.logLoading("&5Particle Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Particle", ex);
        }
    }

    private void loadTagElements() {
        if (Util.IS_RUNNING_SKRIPT_2_10) {
            Util.logLoading("&5Minecraft Tag Elements &cdisabled &r(&7now in Skript&r)");
            return;
        }
        if (!this.config.ELEMENTS_MINECRAFT_TAG) {
            Util.logLoading("&5Minecraft Tag Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.tag");
            Util.logLoading("&5Minecraft Tag Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Minecraft Tag", ex);
        }
    }

    private void loadRayTraceElements() {
        if (!this.config.ELEMENTS_RAYTRACE) {
            Util.logLoading("&5RayTrace Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.raytrace");
            Util.logLoading("&5RayTrace Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("RayTrace", ex);
        }
    }

    private void loadFishingElements() {
        if (!this.config.ELEMENTS_FISHING) {
            Util.logLoading("&5Fishing Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.fishing");
            Util.logLoading("&5Fishing Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Fishing", ex);
        }
    }

    private void loadDisplayEntityElements() {
        if (!this.config.ELEMENTS_DISPLAY) {
            Util.logLoading("&5Display Entity Elements &cdisabled via config");
            return;
        }
        if (!Skript.isRunningMinecraft(1, 19, 4)) {
            Util.logLoading("&5Display Entity Elements &cdisabled &7(&eRequires Minecraft 1.19.4+&7)");
            return;
        }
        if (!Skript.classExists("org.bukkit.entity.TextDisplay$TextAlignment")) {
            Util.logLoading("&5Display Entity Elements &cdisabled due to a Bukkit API change!");
            Util.logLoading("&7- &eYou need to update your server to fix this issue!");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.display");
            Util.logLoading("&5Display Entity Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Display Entity", ex);
        }
    }

    private void loadDamageSourceElements() {
        if (!this.config.ELEMENTS_DAMAGE_SOURCE) {
            Util.logLoading("&5Damage Source Elements &cdisabled via config");
            return;
        }
        if (!Skript.classExists("org.bukkit.damage.DamageSource")) {
            Util.logLoading("&5Damage Source Elements &cdisabled &7(&eRequires Minecraft 1.20.4+&7)");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.damagesource");
            Util.logLoading("&5Damage Source Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Damage Source", ex);
        }
    }

    private void loadItemComponentElements() {
        if (!this.config.ELEMENTS_ITEM_COMPONENT) {
            Util.logLoading("&5Item Component Elements &cdisabled via config");
            return;
        }
        if (!Skript.classExists("io.papermc.paper.datacomponent.DataComponentTypes")) {
            Util.logLoading("&5Item Component Elements &cdisabled &7(&eRequires Paper 1.21.3+&7)");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.itemcomponent");
            Util.logLoading("&5Item Component Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Item Component", ex);
        }
    }

    private void loadTestingElements() {
        if (!TestMode.ENABLED) return;
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.testing");
            Util.logLoading("&5Testing Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Testing", ex);
        }
    }

    private void loadRegistryElements() {
        // We won't use a config for this
        // Not sure which truly came last
        if (!Skript.classExists("io.papermc.paper.registry.tag.TagKey") ||
            !Skript.classExists("io.papermc.paper.registry.RegistryKey")) {
            Util.logLoading("&5Registry Elements &cdisabled &7(&eRequires Paper 1.21+&7)");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.registry");
            Util.logLoading("&5Registry Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Registry", ex);
        }
    }

    private void loadSwitchCaseElements() {
        if (!this.config.ELEMENTS_SWITCH_CASE) {
            Util.logLoading("&5SwitchCase Elements &cdisabled via config");
            return;
        }
        try {
            this.addon.loadClasses("com.shanebeestudios.skbee.elements.switchcase");
            Util.logLoading("&5SwitchCase Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("SwitchCase", ex);
        }
    }

    private void loadPropertyElements() {
        if (!this.config.ELEMENTS_PROPERTY) {
            Util.logLoading("&5Property elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("com.shanebeestudios.skbee.elements.property");
            Util.logLoading("&5Property Elements &asuccessfully loaded");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ThrowableNotThrown")
    private void logFailure(String element, Exception ex) {
        Skript.exception(ex);
        Util.logLoading("&e%s Elements &7failed to load due to &r'&c%s&r'", element, ex.getCause().getMessage());
    }

    public boolean isTextComponentEnabled() {
        return this.textComponentEnabled;
    }

    private boolean isPlugmanReloaded() {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.toString().contains("rylinaux.plugman.command.")) {
                return true;
            }
        }
        return false;
    }

}
