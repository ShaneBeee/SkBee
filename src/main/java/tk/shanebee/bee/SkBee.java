package tk.shanebee.bee;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tk.shanebee.bee.api.NBTApi;
import tk.shanebee.bee.config.Config;
import tk.shanebee.bee.elements.board.listener.PlayerBoardListener;
import tk.shanebee.bee.metrics.Metrics;

import java.io.IOException;

public class SkBee extends JavaPlugin {

    private static SkBee instance;
    private NBTApi nbtApi;
    private PluginManager pm;
    private Config config;
    private SkriptAddon addon;

    @Override
    public void onEnable() {
        instance = this;
        this.nbtApi = new NBTApi();
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
            addon.loadClasses("tk.shanebee.bee.elements.nbt");
            nbtApi.forceLoadNBT();
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
        // load bound stuff
    }

    private void loadStructureElements() {
        if (!this.config.ELEMENTS_STRUCTURE) {
            log("&5Structure Elements &cdisabled via config");
            return;
        }
        try {
            addon.loadClasses("tk.shanebee.bee.elements.structure");
            log("&5Structure Elements &asuccessfully loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            pm.disablePlugin(this);
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
    }

    public static SkBee getPlugin() {
        return instance;
    }

    public Config getPluginConfig() {
        return this.config;
    }

    public NBTApi getNbtApi() {
        return nbtApi;
    }

    public static void log(String log) {
        String prefix = "&7[&bSk&3Bee&7] ";
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + log));
    }


}
