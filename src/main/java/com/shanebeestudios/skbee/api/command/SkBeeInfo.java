package com.shanebeestudios.skbee.api.command;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptUpdater;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.property.PropertyPrinter;
import com.shanebeestudios.skbee.api.registration.JsonDocGenerator;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import static com.shanebeestudios.skbee.api.util.Util.sendColMsg;

/**
 * Base SkBee command
 * // TODO I would like to eventually use Paper's proper command system
 */
public class SkBeeInfo implements BasicCommand {

    private final SkBee plugin;
    private static final List<String> commands = List.of("info", "debug");
    private static final List<String> debugs = List.of("enable", "disable");
    private final PluginDescriptionFile desc;
    private final Config config;

    @SuppressWarnings("deprecation")
    public SkBeeInfo(SkBee plugin) {
        this.plugin = plugin;
        this.desc = plugin.getDescription();
        this.config = plugin.getPluginConfig();
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String[] args) {
        if (args.length <= 1) {
            return commands;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return debugs;
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        if (args.length == 0) {
            StringJoiner joiner = new StringJoiner("/");
            commands.forEach(joiner::add);
            Util.sendColMsg(sender, "Usage: /skbee <" + joiner + ">");
        } else {
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
                        sendColMsg(sender, "&7- &b" + name + " v" + addon.plugin.getPluginMeta().getVersion());
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
            // PROPERTY PRINTER
            else if (args[0].equalsIgnoreCase("properties")) {
                PropertyPrinter.printAll();
            }

            // JSON DOCS
            else if (args[0].equalsIgnoreCase("docs")) {
                JsonDocGenerator docs = new JsonDocGenerator(this.plugin, this.plugin.getRegistration());
                docs.generateDocs();
            }
        }
    }

    @Override
    public @Nullable String permission() {
        return "skbee.command";
    }

}
