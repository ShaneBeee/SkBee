package com.shanebeestudios.skbee;

import ch.njol.skript.Skript;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.util.Version;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.fastboard.FastBoardManager;
import com.shanebeestudios.skbee.api.listener.EntityListener;
import com.shanebeestudios.skbee.api.listener.NBTListener;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.property.PropertyRegistry;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.LoggerBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.advancement.AdvancementElementRegistration;
import com.shanebeestudios.skbee.elements.bossbar.BossbarElementRegistration;
import com.shanebeestudios.skbee.elements.bound.BoundElementRegistration;
import com.shanebeestudios.skbee.elements.damagesource.DamageSourceElementRegistration;
import com.shanebeestudios.skbee.elements.dialog.DialogElementRegestration;
import com.shanebeestudios.skbee.elements.fastboard.FastboardElementRegistration;
import com.shanebeestudios.skbee.elements.gameevent.GameEventElementRegistration;
import com.shanebeestudios.skbee.elements.generator.ChunkGeneratorElementRegistration;
import com.shanebeestudios.skbee.elements.itemcomponent.ItemComponentElementRegistration;
import com.shanebeestudios.skbee.elements.nbt.NBTElementRegistration;
import com.shanebeestudios.skbee.elements.other.OtherElementRegistration;
import com.shanebeestudios.skbee.elements.property.PropertyElementRegistration;
import com.shanebeestudios.skbee.elements.raytrace.RayTraceElementRegistration;
import com.shanebeestudios.skbee.elements.recipe.RecipeElementRegistration;
import com.shanebeestudios.skbee.elements.registry.RegistryElementRegistration;
import com.shanebeestudios.skbee.elements.scoreboard.ScoreboardElementRegistration;
import com.shanebeestudios.skbee.elements.testing.TestingElementRegistration;
import com.shanebeestudios.skbee.elements.text.TextElementRegistration;
import com.shanebeestudios.skbee.elements.tickmanager.TickManagerElementRegistration;
import com.shanebeestudios.skbee.elements.worldcreator.WorldCreatorElementRegistration;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldConfig;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Objective;
import org.skriptlang.skript.addon.SkriptAddon;

/**
 * @hidden
 */
public class AddonLoader {

    private final PluginManager pluginManager;
    private final SkBee plugin;
    private final Registration registration = new Registration();
    private final Config config;
    private final Plugin skriptPlugin;

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
        if (skriptVersion.isSmallerThan(new Version(2, 11, 999))) {
            Util.logLoading("&cDependency Skript outdated, Skript elements cannot load.");
            Util.logLoading("&eSkBee requires Skript 2.12+ but found Skript " + skriptVersion);
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
        loadSkriptElements();
        return true;
    }

    private void loadSkriptElements() {
        SkriptAddon addon = Skript.instance().registerAddon(SkBeeAddonModule.class, "SkBee");
        addon.localizer().setSourceDirectories("lang", null);

        // Load first as these are the base for many things
        loadOtherElements();
        loadNBTElements();

        // Load in alphabetical order (to make "/skbee info" easier to read)
        loadAdvancementElements();
        loadBossBarElements();
        loadBoundElements();
        loadDamageSourceElements();
        loadDialogElements();
//        loadDisplayEntityElements();
        loadFastboardElements();
//        loadFishingElements();
        loadGameEventElements();
        loadItemComponentElements();
        loadPropertyElements();
        loadRayTraceElements();
        loadRecipeElements();
        loadRegistryElements();
        loadScoreboardElements();
//        loadStatisticElements();
//        loadStructureElements();
//        loadSwitchCaseElements();
        loadTextElements();
        loadTickManagerElements();
//        loadVillagerElements();
//        loadVirtualFurnaceElements();
        loadWorldCreatorElements();
        loadChunkGenElements();
        loadTestingElements();

        // Load elements into Skript
        SkBeeAddonModule module = new SkBeeAddonModule(this.registration);
        addon.loadModules(module);

        // ELEMENT COUNT
        int typeCount = this.registration.getTypes().size();
        int structureCount = this.registration.getStructures().size();
        int eventCount = this.registration.getEvents().size();
        int sectionCount = this.registration.getSections().size();
        int effectCount = this.registration.getEffects().size();
        int expressionCount = this.registration.getExpressions().size();
        int conditionCount = this.registration.getConditions().size();
        int propertyCount = PropertyRegistry.properties().size();
        int total = eventCount + effectCount + expressionCount + conditionCount + sectionCount + typeCount + structureCount + propertyCount;

        Util.log("Loaded SkBee (%s) elements:", total);
        Util.log(" - %s types", typeCount);
        Util.log(" - %s structures", structureCount);
        Util.log(" - %s events", eventCount);
        Util.log(" - %s sections", sectionCount);
        Util.log(" - %s effects", effectCount);
        Util.log(" - %s expressions", expressionCount);
        Util.log(" - %s conditions", conditionCount);
        if (this.config.ELEMENTS_PROPERTY) {
            Util.log(" - %s properties", propertyCount);
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
        NBTApi.initializeAPI(this.config);
        if (!NBTApi.isEnabled()) {
            String ver = Skript.getMinecraftVersion().toString();
            Util.logLoading("&5NBT Elements &cDISABLED!");
            Util.logLoading(" - Your server version [&b" + ver + "&7] is not currently supported by the NBT-API");
            Util.logLoading(" - This is not a bug!");
            Util.logLoading(" - NBT elements will resume once the API is updated to work with [&b" + ver + "&7]");
            return;
        }
        try {
            NBTElementRegistration.register(this.registration);
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
            RecipeElementRegistration.register(this.registration);
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
            this.pluginManager.registerEvents(new FastBoardManager(this.plugin, true), this.plugin);
            FastboardElementRegistration.register(this.registration);
            Util.logLoading("&5Fastboard&7[&bAdventure&7] &5Elements &asuccessfully loaded");
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
        if (Util.IS_RUNNING_FOLIA) {
            Util.logLoading("&5Scoreboard Elements &cdisabled &7(&eCurrently not supported on Folia&7)");
            return;
        }
        try {
            ScoreboardElementRegistration.register(this.registration);
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
        try {
            TickManagerElementRegistration.register(this.registration);
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
            BoundElementRegistration.register(this.registration);
            Util.logLoading("&5Bound Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Bound", ex);
        }
    }

    private void loadTextElements() {
        if (Classes.getClassInfoNoError("textcomponent") != null) {
            Util.logLoading("&5Text Component Elements &cdisabled");
            Util.logLoading("&7It appears another Skript addon may have registered Text Component syntax.");
            Util.logLoading("&7To use SkBee Text Components, please remove the addon which has registered Text Components already.");
            return;
        }
        try {
            TextElementRegistration.register(this.registration);
            Util.logLoading("&5Text Component Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Text Component", ex);
        }
    }

    //
//    private void loadStructureElements() {
//        if (!this.config.ELEMENTS_STRUCTURE) {
//            Util.logLoading("&5Structure Elements &cdisabled via config");
//            return;
//        }
//
//        this.plugin.structureManager = new StructureManager();
//        try {
//            this.addon.loadClasses("com.shanebeestudios.skbee.elements.structure");
//            Util.logLoading("&5Structure Elements &asuccessfully loaded");
//        } catch (Exception ex) {
//            logFailure("Structure", ex);
//        }
//    }
//
//    private void loadVirtualFurnaceElements() {
//        if (!this.config.ELEMENTS_VIRTUAL_FURNACE) {
//            Util.logLoading("&5Virtual Furnace Elements &cdisabled via config");
//            return;
//        }
//        try {
//            this.plugin.virtualFurnaceAPI = new VirtualFurnaceAPI(this.plugin, true);
//            pluginManager.registerEvents(new VirtualFurnaceListener(), this.plugin);
//            this.addon.loadClasses("com.shanebeestudios.skbee.elements.virtualfurnace");
//            Util.logLoading("&5Virtual Furnace Elements &asuccessfully loaded");
//        } catch (Exception ex) {
//            logFailure("Virtual Furnace", ex);
//        }
//    }

    private void loadOtherElements() {
        this.pluginManager.registerEvents(new EntityListener(), this.plugin);
        OtherElementRegistration.register(this.registration);
    }

    private void loadWorldCreatorElements() {
        if (!this.config.ELEMENTS_WORLD_CREATOR) {
            Util.logLoading("&5World Creator Elements &cdisabled via config");
            return;
        }
        if (Util.IS_RUNNING_FOLIA) {
            Util.logLoading("&5World Creator Elements &cdisabled &7(&eCurrently not supported on Folia&7)");
            return;
        }
        try {
            this.plugin.beeWorldConfig = new BeeWorldConfig(this.plugin);
            WorldCreatorElementRegistration.register(this.registration);
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
        if (Util.IS_RUNNING_FOLIA) {
            Util.logLoading("&5Chunk Generator Elements &cdisabled &7(&eCurrently not supported on Folia&7)");
            return;
        }
        try {
            ChunkGeneratorElementRegistration.register(this.registration);
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
            GameEventElementRegistration.register(this.registration);
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
            BossbarElementRegistration.register(this.registration);
            Util.logLoading("&5BossBar Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("BossBar", ex);
        }

    }

//    private void loadStatisticElements() {
//        if (!this.config.ELEMENTS_STATISTIC) {
//            Util.logLoading("&5Statistic Elements &cdisabled via config");
//            return;
//        }
//        if (Classes.getClassInfoNoError("statistic") != null || Classes.getExactClassInfo(Statistic.class) != null) {
//            Util.logLoading("&5Statistic Elements &cdisabled");
//            Util.logLoading("&7It appears another Skript addon may have registered Statistic syntax.");
//            Util.logLoading("&7To use SkBee Statistics, please remove the addon which has registered Statistic already.");
//            return;
//        }
//        try {
//            this.addon.loadClasses("com.shanebeestudios.skbee.elements.statistic");
//            Util.logLoading("&5Statistic Elements &asuccessfully loaded");
//        } catch (Exception ex) {
//            logFailure("Statistic", ex);
//        }
//    }
//
//    private void loadVillagerElements() {
//        if (!this.config.ELEMENTS_VILLAGER) {
//            Util.logLoading("&5Villager Elements &cdisabled via config");
//            return;
//        }
//        try {
//            this.addon.loadClasses("com.shanebeestudios.skbee.elements.villager");
//            Util.logLoading("&5Villager Elements &asuccessfully loaded");
//        } catch (Exception ex) {
//            logFailure("Villager", ex);
//        }
//    }

    private void loadAdvancementElements() {
        if (!this.config.ELEMENTS_ADVANCEMENT) {
            Util.logLoading("&5Advancement Elements &cdisabled via config");
            return;
        }
        try {
            AdvancementElementRegistration.register(this.registration);
            Util.logLoading("&5Advancement Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Advancement", ex);
        }
    }

    private void loadRayTraceElements() {
        if (!this.config.ELEMENTS_RAYTRACE) {
            Util.logLoading("&5RayTrace Elements &cdisabled via config");
            return;
        }
        try {
            RayTraceElementRegistration.register(this.registration);
            Util.logLoading("&5RayTrace Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("RayTrace", ex);
        }
    }
//
//    private void loadFishingElements() {
//        if (!this.config.ELEMENTS_FISHING) {
//            Util.logLoading("&5Fishing Elements &cdisabled via config");
//            return;
//        }
//        try {
//            this.addon.loadClasses("com.shanebeestudios.skbee.elements.fishing");
//            Util.logLoading("&5Fishing Elements &asuccessfully loaded");
//        } catch (Exception ex) {
//            logFailure("Fishing", ex);
//        }
//    }
//
//    private void loadDisplayEntityElements() {
//        if (!this.config.ELEMENTS_DISPLAY) {
//            Util.logLoading("&5Display Entity Elements &cdisabled via config");
//            return;
//        }
//        try {
//            this.addon.loadClasses("com.shanebeestudios.skbee.elements.display");
//            Util.logLoading("&5Display Entity Elements &asuccessfully loaded");
//        } catch (Exception ex) {
//            logFailure("Display Entity", ex);
//        }
//    }

    private void loadDamageSourceElements() {
        if (!this.config.ELEMENTS_DAMAGE_SOURCE) {
            Util.logLoading("&5Damage Source Elements &cdisabled via config");
            return;
        }
        try {
            DamageSourceElementRegistration.register(this.registration);
            Util.logLoading("&5Damage Source Elements &asuccessfully loaded");
            Util.log("&7 - Do note these elements are in Skript as 'Experimental'");
            Util.log("&7 - If issues arise, disable this feature and use Skript's elements instead");
        } catch (Exception ex) {
            logFailure("Damage Source", ex);
        }
    }

    private void loadItemComponentElements() {
        if (!this.config.ELEMENTS_ITEM_COMPONENT) {
            Util.logLoading("&5Item Component Elements &cdisabled via config");
            return;
        }
        try {
            ItemComponentElementRegistration.register(this.registration);
            Util.logLoading("&5Item Component Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Item Component", ex);
        }
    }

    private void loadTestingElements() {
        if (!TestMode.ENABLED) return;
        try {
            TestingElementRegistration.register(this.registration);
            Util.logLoading("&5Testing Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Testing", ex);
        }
    }

    private void loadRegistryElements() {
        try {
            RegistryElementRegistration.register(this.registration);
            Util.logLoading("&5Registry Elements &asuccessfully loaded");
        } catch (Exception ex) {
            logFailure("Registry", ex);
        }
    }

    //    private void loadSwitchCaseElements() {
//        if (!this.config.ELEMENTS_SWITCH_CASE) {
//            Util.logLoading("&5SwitchCase Elements &cdisabled via config");
//            return;
//        }
//        try {
//            this.addon.loadClasses("com.shanebeestudios.skbee.elements.switchcase");
//            Util.logLoading("&5SwitchCase Elements &asuccessfully loaded");
//        } catch (Exception ex) {
//            logFailure("SwitchCase", ex);
//        }
//    }

    private void loadPropertyElements() {
        if (!this.config.ELEMENTS_PROPERTY) {
            Util.logLoading("&5Property elements &cdisabled via config");
            return;
        }
        PropertyElementRegistration.register(this.registration);
        Util.logLoading("&5Property Elements &asuccessfully loaded");
    }

    private void loadDialogElements() {
        if (!this.config.ELEMENTS_DIALOG) {
            Util.logLoading("&5Dialog elements &cdisabled via config");
            return;
        }
        if (!Util.IS_RUNNING_MC_1_21_7) {
            Util.logLoading("&5Dialog elements &cdisabled &7(&rRequires Paper 1.21.7+&7)");
            return;
        }
        DialogElementRegestration.register(this.registration);
        Util.logLoading("&5Dialog Elements &asuccessfully loaded");
    }

    @SuppressWarnings("ThrowableNotThrown")
    private void logFailure(String element, Exception ex) {
        Skript.exception(ex);
        Util.logLoading("&e%s Elements &7failed to load due to &r'&c%s&r'", element, ex.getCause().getMessage());
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
