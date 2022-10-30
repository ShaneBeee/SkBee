package tk.shanebee.bee;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Version;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tk.shanebee.bee.api.NBTApi;
import tk.shanebee.bee.api.listener.BoundBorderListener;
import tk.shanebee.bee.api.listener.EntityListener;
import tk.shanebee.bee.api.listener.NBTListener;
import tk.shanebee.bee.api.util.LoggerBee;
import tk.shanebee.bee.api.util.Util;
import tk.shanebee.bee.config.Config;
import tk.shanebee.bee.elements.board.listener.PlayerBoardListener;
import tk.shanebee.bee.elements.board.objects.Board;
import tk.shanebee.bee.elements.bound.config.BoundConfig;
import tk.shanebee.bee.elements.bound.objects.Bound;
import tk.shanebee.bee.elements.worldcreator.objects.BeeWorldConfig;
import tk.shanebee.bee.metrics.Metrics;

import java.io.IOException;

/**
 * Main class for SkBee
 */
public class SkBee extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(Bound.class, "Bound");
    }

    private static SkBee instance;
    private NBTApi nbtApi;
    private PluginManager pm;
    private Config config;
    private BoundConfig boundConfig = null;
    private SkriptAddon addon;
    private BeeWorldConfig beeWorldConfig;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        this.config = new Config(this);
        this.nbtApi = new NBTApi();
        MinecraftVersion.replaceLogger(LoggerBee.getLogger());
        this.pm = Bukkit.getPluginManager();
        PluginDescriptionFile desc = getDescription();

        final Plugin SKRIPT = pm.getPlugin("Skript");
        if (SKRIPT != null && SKRIPT.isEnabled() && Skript.isAcceptRegistrations()) {
            if (Skript.getVersion().isSmallerThan(new Version(2, 6, 2))) {
                Util.log("Skript 2.6.2+ is required for this version of SkBee");
                pm.disablePlugin(this);
                return;
            }
            if (Skript.isRunningMinecraft(1, 13)) {
                Util.log("This version of SkBee is meant for legacy server versions.");
                Util.log("Use an updated version of SkBee for MC 1.13+");
                pm.disablePlugin(this);
                return;
            }
            addon = Skript.registerAddon(this);
            addon.setLanguageFileDirectory("lang");

            // Load Skript elements
            loadNBTElements();
            loadBoardElements();
            loadBoundElements();
            loadTextElements();
            loadOtherElements();
            loadWorldCreatorElements();

            // Beta check + notice
            if (desc.getVersion().contains("Beta")) {
                Util.log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
                Util.log("&ehttps://github.com/ShaneBeee/SkBee/issues");
            }
        } else {
            Util.log("&cDependency Skript was not found, plugin disabling");
            pm.disablePlugin(this);
            return;
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
        try {
            nbtApi.forceLoadNBT();
        } catch (Exception ignore) {
            String ver = Skript.getMinecraftVersion().toString();
            Util.log("&5NBT Elements &cDISABLED!");
            Util.log(" - Your server version [&b" + ver + "&7] is not currently supported by the NBT-API");
            Util.log(" - This is not a bug!");
            Util.log(" - NBT elements will resume once the API is updated to work with [&b" + ver + "&7]");
            return;
        }
        try {
            addon.loadClasses("tk.shanebee.bee.elements.nbt");
            if (NBTApi.SUPPORTS_BLOCK_NBT) {
                pm.registerEvents(new NBTListener(this), this);
            }
            Util.log("&5NBT Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadBoardElements() {
        if (Skript.isRunningMinecraft(1, 8, 8)) {
            if (!this.config.ELEMENTS_BOARD) {
                Util.log("&5Scoreboard Elements &cdisabled via config");
                return;
            }
            try {
                addon.loadClasses("tk.shanebee.bee.elements.board");
                pm.registerEvents(new PlayerBoardListener(), this);
                // If there are players online during a reload, let's give them a board
                Bukkit.getOnlinePlayers().forEach(Board::createBoard);
                Util.log("&5Scoreboard Elements &asuccessfully loaded");
            } catch (IOException ex) {
                ex.printStackTrace();
                pm.disablePlugin(this);
            }
        } else {
            Util.log("&5Scoreboard Elements &cdisabled");
            Util.log("&7 - Scoreboard elements are only available on 1.8.8+");
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
            addon.loadClasses("tk.shanebee.bee.elements.bound");
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
            addon.loadClasses("tk.shanebee.bee.elements.text");
            Util.log("&5Text Component Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadOtherElements() {
        try {
            pm.registerEvents(new EntityListener(), this);
            addon.loadClasses("tk.shanebee.bee.elements.other");
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
            addon.loadClasses("tk.shanebee.bee.elements.worldcreator");
            Util.log("&5World Creator Elements &asuccessfully loaded");
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
        Board.clearBoards();
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

}
