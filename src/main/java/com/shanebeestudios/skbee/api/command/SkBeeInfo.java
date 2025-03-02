package com.shanebeestudios.skbee.api.command;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptUpdater;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.shanebeestudios.skbee.api.util.Util.sendColMsg;

public class SkBeeInfo implements TabExecutor {

    private final PluginDescriptionFile desc;
    private final Config config;

    @SuppressWarnings("deprecation")
    public SkBeeInfo(SkBee plugin) {
        this.desc = plugin.getDescription();
        this.config = plugin.getPluginConfig();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length > 0) {
            // INFO COMMAND
            if (args[0].equalsIgnoreCase("info")) {
                sendColMsg(sender, "&7--- [&bSkBee Loading Info&7] ---");

                // SkBee element info
                Util.getDebugs().forEach(debug -> sendColMsg(sender, "- &7" + debug));
                sendColMsg(sender, "&7--- [&bServer Info&7] ---");

                // Server version
                sendColMsg(sender, "&7Server Version: &b" + Bukkit.getName() + " " + Bukkit.getVersion());

                // Skript version
                SkriptUpdater updater = Skript.getInstance().getUpdater();
                String flavor = "&cunknown-flavor";
                if (updater != null) {
                    flavor = updater.getCurrentRelease().flavor;
                    if (flavor.equalsIgnoreCase("skriptlang-github")) flavor = "&a" + flavor;
                    else flavor = "&e" + flavor;
                }
                sendColMsg(sender, "&7Skript Version: &b%s &7(%s&7)", Skript.getVersion(), flavor);

                // Addon versions
                sendColMsg(sender, "&7Skript Addons:");
                Skript.getAddons().forEach(addon -> {
                    String name = addon.getName();
                    if (!name.contains("SkBee")) {
                        sendColMsg(sender, "&7- &b" + name + " v" + addon.plugin.getDescription().getVersion());
                    }
                });

                // SkBee info
                sendColMsg(sender, "&7SkBee Version: &b" + desc.getVersion());
                sendColMsg(sender, "&7SkBee Website: &b" + desc.getWebsite());
            }
            // DEBUG COMMAND
            else if (args[0].equalsIgnoreCase("debug")) {
                if (args.length > 1 && args[1].equalsIgnoreCase("enable")) {
                    Util.sendColMsg(sender, "Debug mode is now &aenabled!");
                    this.config.settings_debug = true;
                } else if (args.length > 1 && args[1].equalsIgnoreCase("disable")) {
                    Util.sendColMsg(sender, "Debug mode is now &cdisabled!");
                    this.config.settings_debug = false;
                } else {
                    String enabled = this.config.settings_debug ? "&aenabled" : "&cdisabled";
                    Util.sendColMsg(sender, "Debug mode is currently %s", enabled);
                }
            }
        }
        return true;
    }

    private static final List<String> commands = List.of("info", "debug");
    private static final List<String> debugs = List.of("enable", "disable");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return StringUtil.copyPartialMatches(args[1], debugs, new ArrayList<>());
        }
        return null;
    }

}
