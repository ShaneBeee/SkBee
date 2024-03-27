package com.shanebeestudios.skbee.api.command;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.profiler.Profile;
import com.shanebeestudios.skbee.api.profiler.Profiler;
import com.shanebeestudios.skbee.api.profiler.Profilers;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.shanebeestudios.skbee.api.util.Util.sendColMsg;

public class SkBeeInfo implements TabExecutor {

    private static final List<String> commands = List.of("info", "profiler");
    private static final List<String> profilerCommands = List.of("send", "start", "stop");

    private final PluginDescriptionFile desc;

    @SuppressWarnings("deprecation")
    public SkBeeInfo(SkBee plugin) {
        this.desc = plugin.getDescription();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("info")) {
                sendColMsg(sender, "&7--- [&bSkBee Loading Info&7] ---");
                Util.getDebugs().forEach(debug -> sendColMsg(sender, "- &7" + debug));
                sendColMsg(sender, "&7--- [&bServer Info&7] ---");
                sendColMsg(sender, "&7Server Version: &b" + Bukkit.getVersion());
                sendColMsg(sender, "&7Skript Version: &b" + Skript.getVersion());
                sendColMsg(sender, "&7Skript Addons:");
                Skript.getAddons().forEach(addon -> {
                    String name = addon.getName();
                    if (!name.contains("SkBee")) {
                        sendColMsg(sender, "&7- &b" + name + " v" + addon.plugin.getDescription().getVersion());
                    }
                });
                sendColMsg(sender, "&7SkBee Version: &b" + desc.getVersion());
                sendColMsg(sender, "&7SkBee Website: &b" + desc.getWebsite());
            } else if (args[0].equalsIgnoreCase("profiler")) {
                if (args.length > 1) {
                    Collection<Profiler> profilers = Profilers.getAllProfilers();
                    if (args.length > 2) {
                        Profiler profiler = Profilers.getByType(args[2]);
                        if (profiler != null) profilers = List.of(profiler);
                    }
                    switch (args[1]) {
                        case "stop":
                            sendProfilerMessage(sender, "Profiler stopped.");
                            logProfilersToConsole(sender, profilers);
                            Profilers.setEnabled(false);
                            break;
                        case "start":
                            Profilers.setEnabled(true);
                            sendProfilerMessage(sender, "Profiler started.");
                            break;
                        case "send":
                            logProfilersToConsole(sender, profilers);
                            break;
                        default:
                            sendColMsg(sender, "Available profiler commands: " + profilerCommands);
                    }
                }
            } else {
                sendColMsg(sender, "Available subcommands: " + commands);
            }
        } else {
            sendColMsg(sender, "Required subcommand options: " + commands);
        }
        return true;
    }

    private static void sendProfilerMessage(CommandSender sender, String format, Object... objects) {
        sendColMsg(sender, "&7[&bSk&3Bee &bProfiler&7] " + format, objects);
    }

    private static void logProfilerMessage(String format, Object... objects) {
        sendProfilerMessage(Bukkit.getConsoleSender(), format, objects);
    }

    private static void logProfilersToConsole(CommandSender sender, Collection<Profiler> profilers) {
        List<Profile> activeProfiles = new ArrayList<>();
        profilers.forEach(profiler -> activeProfiles.addAll(profiler.getProfileMap()));
        if (!activeProfiles.isEmpty()) {
            logProfilerMessage("Profiles:");
            activeProfiles.forEach(profile -> logProfilerMessage("&7 - %s", profile.toString()));
            if (sender instanceof Player) {
                sendProfilerMessage(sender, "Profiles are now available in console.");
            }
        } else {
            sendProfilerMessage(sender, "&cNo profiles avaiable!");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("profiler")) {
            return StringUtil.copyPartialMatches(args[1], profilerCommands, new ArrayList<>());
        } else if (args.length == 3 && args[1].equalsIgnoreCase("send")) {
            return StringUtil.copyPartialMatches(args[2], Profilers.getTypes(), new ArrayList<>());
        }
        return null;
    }

}
