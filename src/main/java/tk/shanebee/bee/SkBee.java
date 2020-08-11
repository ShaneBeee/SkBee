package tk.shanebee.bee;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.github.shynixn.structureblocklib.bukkit.core.VersionSupport;
import com.shanebeestudios.vf.api.VirtualFurnaceAPI;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tk.shanebee.bee.api.NBTApi;
import tk.shanebee.bee.api.listener.BoundBorderListener;
import tk.shanebee.bee.api.util.LoggerBee;
import tk.shanebee.bee.config.Config;
import tk.shanebee.bee.elements.board.listener.PlayerBoardListener;
import tk.shanebee.bee.elements.bound.config.BoundConfig;
import tk.shanebee.bee.elements.bound.objects.Bound;
import tk.shanebee.bee.elements.virtualfurnace.listener.VirtualFurnaceListener;
import tk.shanebee.bee.metrics.Metrics;

import java.io.IOException;

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
    private VirtualFurnaceAPI virtualFurnaceAPI;

    @Override
    public void onEnable() {
        instance = this;
        this.nbtApi = new NBTApi();
        MinecraftVersion.logger = LoggerBee.getLogger();
        this.pm = Bukkit.getPluginManager();
        this.config = new Config(this);
        PluginDescriptionFile desc = getDescription();

        if ((pm.getPlugin("Skript") != null) && Skript.isAcceptRegistrations()) {
            addon = Skript.registerAddon(this);

            // Load Skript elements
            loadNBTElements();
            loadRecipeElements();
            loadBoardElements();
            loadBoundElements();
            loadStructureElements();
            loadOtherElements();
            loadVirtualFurnaceElements();

            // Beta check + notice
            if (desc.getVersion().contains("Beta")) {
                log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
                log("&ehttps://github.com/ShaneBeee/SkBee/issues");
            }
        } else {
            log("&cDependency Skript was not found, plugin disabling");
            pm.disablePlugin(this);
            return;
        }
        loadMetrics();
        log("&aSuccessfully enabled v" + desc.getVersion());
    }

    private void loadNBTElements() {
        if (!this.config.ELEMENTS_NBT) {
            log("&5NBT Elements &cdisabled via config");
            return;
        }
        try {
            nbtApi.forceLoadNBT();
        } catch (Exception ignore) {
            String ver = Skript.getMinecraftVersion().toString();
            log("&5NBT Elements &cDISABLED!");
            log(" - Your server version [&b" + ver + "&7] is not currently supported by the NBT-API");
            log(" - This is not a bug!");
            log(" - NBT elements will resume once the API is updated to work with [&b" + ver + "&7]");
            return;
        }
        try {
            addon.loadClasses("tk.shanebee.bee.elements.nbt");
            log("&5NBT Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadRecipeElements() {
        if (Skript.isRunningMinecraft(1, 13)) {
            if (!this.config.ELEMENTS_RECIPE) {
                log("&5Recipe Elements &cdisabled via config");
                return;
            }
            try {
                addon.loadClasses("tk.shanebee.bee.elements.recipe");
                log("&5Recipe Elements &asuccessfully loaded");
            } catch (IOException ex) {
                ex.printStackTrace();
                pm.disablePlugin(this);
            }
        } else {
            log("&5Recipe Elements &cdisabled");
            log("&7 - Recipe elements are only available on 1.13+");
        }
    }

    private void loadBoardElements() {
        if (Skript.isRunningMinecraft(1, 13)) {
            if (!this.config.ELEMENTS_BOARD) {
                log("&5Scoreboard Elements &cdisabled via config");
                return;
            }
            try {
                addon.loadClasses("tk.shanebee.bee.elements.board");
                pm.registerEvents(new PlayerBoardListener(), this);
                log("&5Scoreboard Elements &asuccessfully loaded");
            } catch (IOException ex) {
                ex.printStackTrace();
                pm.disablePlugin(this);
            }
        } else {
            log("&5Scoreboard Elements &cdisabled");
            log("&7 - Scoreboard elements are only available on 1.13+");
        }
    }

    private void loadBoundElements() {
        if (!this.config.ELEMENTS_BOUND) {
            log("&5Bound Elements &cdisabled via config");
            return;
        }
        try {
            this.boundConfig = new BoundConfig(this);
            pm.registerEvents(new BoundBorderListener(this), this);
            addon.loadClasses("tk.shanebee.bee.elements.bound");
            log("&5Bound Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    private void loadStructureElements() {
        if (Skript.isRunningMinecraft(1, 9, 4)) {
            if (!this.config.ELEMENTS_STRUCTURE) {
                log("&5Structure Elements &cdisabled via config");
                return;
            }
            // Disable if StructureBlockLib is not currently updated for this server version
            if (VersionSupport.getServerVersion() == null) {
                String ver = Skript.getMinecraftVersion().toString();
                log("&5Structure Elements &cDISABLED!");
                log(" - Your server version [&b" + ver + "&7] is not currently supported by the StructureBlock API");
                log(" - This is not a bug!");
                log(" - Structure elements will resume once the API is updated to work with [&b" + ver + "&7]");
                return;
            }
            try {
                addon.loadClasses("tk.shanebee.bee.elements.structure");
                log("&5Structure Elements &asuccessfully loaded");
            } catch (IOException ex) {
                ex.printStackTrace();
                pm.disablePlugin(this);
            }
        } else {
            log("&5Structure Elements &cdisabled");
            log("&7 - Structure elements are only available on 1.9.4+");
        }
    }

    private void loadVirtualFurnaceElements() {
        if (Skript.classExists("org.bukkit.persistence.PersistentDataContainer")) {
            if (!this.config.ELEMENTS_VIRTUAL_FURNACE) {
                log("&5Virtual Furnace Elements &cdisabled via config");
                return;
            }
            try {
                this.virtualFurnaceAPI = new VirtualFurnaceAPI(this, true);
                pm.registerEvents(new VirtualFurnaceListener(), this);
                addon.loadClasses("tk.shanebee.bee.elements.virtualfurnace");
                log("&5Virtual Furnace Elements &asuccessfully loaded");
            } catch (IOException e) {
                e.printStackTrace();
                pm.disablePlugin(this);
            }
        } else {
            log("&5Virtual Furnace Elements &cdisabled");
            log("&7 - Virtual Furnace elements are only available on 1.13+");
        }
    }

    private void loadOtherElements() {
        try {
            addon.loadClasses("tk.shanebee.bee.elements.other");
            log("&5Other Elements &asuccessfully loaded");
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
    }

    public static SkBee getPlugin() {
        return instance;
    }

    public Config getPluginConfig() {
        return this.config;
    }

    public BoundConfig getBoundConfig() {
        return this.boundConfig;
    }

    public NBTApi getNbtApi() {
        return nbtApi;
    }

    public VirtualFurnaceAPI getVirtualFurnaceAPI() {
        return virtualFurnaceAPI;
    }

    public static void log(String log) {
        String prefix = "&7[&bSk&3Bee&7] ";
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + log));
    }


}
