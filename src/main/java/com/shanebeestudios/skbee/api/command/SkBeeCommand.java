package com.shanebeestudios.skbee.api.command;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptUpdater;
import com.github.shanebeee.skr.JsonDocGenerator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static com.shanebeestudios.skbee.api.util.Util.sendColMsg;

public class SkBeeCommand {

    public static void registerCommand(SkBee plugin) {
        Config config = plugin.getPluginConfig();

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("skbee")
            .requires(context -> context.getSender().hasPermission("skbee.command"))
            .then(Commands.literal("info")
                .executes(context -> {
                    info(context.getSource().getSender(), plugin);
                    return Command.SINGLE_SUCCESS;
                }))
            .then(Commands.literal("debug")
                .then(Commands.literal("enable")
                    .executes(context -> {
                        Util.sendMiniPrefixed(context.getSource().getSender(), "Debug mode is now <green>enabled!");
                        config.settings_debug = true;
                        return Command.SINGLE_SUCCESS;
                    }))
                .then(Commands.literal("disable")
                    .executes(context -> {
                        Util.sendMiniPrefixed(context.getSource().getSender(), "Debug mode is now <red>disabled!");
                        config.settings_debug = false;
                        return Command.SINGLE_SUCCESS;
                    }))
                .executes(context -> {
                    String enabled = config.settings_debug ? "<green>enabled" : "<red>disabled";
                    Util.sendMiniPrefixed(context.getSource().getSender(), "Debug mode is currently %s", enabled);
                    return Command.SINGLE_SUCCESS;
                }))
            .then(Commands.literal("docs")
                .executes(context -> {
                    JsonDocGenerator docs = new JsonDocGenerator(plugin, plugin.getAddonLoader().getRegistration());
                    docs.generateDocs();
                    return Command.SINGLE_SUCCESS;
                }));

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(command.build());
        });
    }

    private static void info(CommandSender sender, SkBee plugin) {
        PluginMeta pluginMeta = plugin.getPluginMeta();
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
        sendColMsg(sender, "&7SkBee Version: &b" + pluginMeta.getVersion());
        sendColMsg(sender, "&7SkBee Website: &b" + pluginMeta.getWebsite());
    }

}
