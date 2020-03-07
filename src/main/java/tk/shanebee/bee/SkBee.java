package tk.shanebee.bee;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tk.shanebee.bee.api.NBTApi;

import java.io.IOException;

public class SkBee extends JavaPlugin {

    private static SkBee instance;
    private NBTApi nbtApi;

    @Override
    public void onEnable() {
        instance = this;
        this.nbtApi = new NBTApi();
        PluginDescriptionFile desc = getDescription();
        PluginManager pm = Bukkit.getPluginManager();

        if ((pm.getPlugin("Skript") != null) && Skript.isAcceptRegistrations()) {
            SkriptAddon addon = Skript.registerAddon(this);

            try {
                addon.loadClasses("tk.shanebee.bee.elements");
                nbtApi.forceLoadNBT();
            } catch (IOException ex) {
                ex.printStackTrace();
                pm.disablePlugin(this);
                return;
            }
            if (desc.getVersion().contains("Beta")) {
                log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
                log("&ehttps://github.com/ShaneBeee/SkBee/issues");
            }
        } else {
            log("&cDependency Skript was not found, plugin disabling");
            pm.disablePlugin(this);
            return;
        }
        log("&aSuccessfully enabled v" + desc.getVersion());
    }

    @Override
    public void onDisable() {
    }

    public static SkBee getPlugin() {
        return instance;
    }

    public NBTApi getNbtApi() {
        return nbtApi;
    }

    public static void log(String log) {
        String prefix = "&7[&bSk&3Bee&7] ";
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + log));
    }


}
