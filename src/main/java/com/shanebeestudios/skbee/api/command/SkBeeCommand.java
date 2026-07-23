package com.shanebeestudios.skbee.api.command;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptUpdater;
import com.github.shanebeee.skr.JsonDocGenerator;
import com.github.shanebeee.skr.scheduling.TaskUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
                }))
            .then(Commands.literal("features")
                .executes(context -> {
                    features(context.getSource().getSender());
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

    private static void features(CommandSender sender) {
        sendColMsg(sender, "&7--- [&bSkBee bStats Features&7] ---");
        sendColMsg(sender, "&7Fetching data from bStats...");
        TaskUtils.getGlobalScheduler().runTaskAsync(() -> {
            try {
                URL url = new URL("https://bstats.org/api/v1/plugins/6719/charts/features_used/data?maxElements=1");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                // Response is a JSON array; we only requested 1 element so it's [[timestamp, {feature: {true: n, false: n}}]]
                JsonObject root = new Gson().fromJson(reader, JsonObject.class);
                JsonArray drilldownData = root.getAsJsonArray("drilldownData");
                JsonArray seriesData = root.getAsJsonArray("seriesData");
                int totalServers = seriesData.get(seriesData.size() - 1).getAsJsonObject().get("y").getAsInt();

                sendColMsg(sender, "&7Total servers: &b%d", totalServers);

                // Build list of [feature, truePercent]
                // Each entry: {"name": "nbt", "data": [["true", 648], ["false", 3215]]}
                List<Map.Entry<String, double[]>> results = new ArrayList<>();
                for (JsonElement element : drilldownData) {
                    JsonObject feature = element.getAsJsonObject();
                    String name = feature.get("name").getAsString();
                    double trueCount = 0, falseCount = 0;
                    for (JsonElement pair : feature.getAsJsonArray("data")) {
                        JsonArray p = pair.getAsJsonArray();
                        if (p.get(0).getAsString().equals("true")) trueCount = p.get(1).getAsDouble();
                        else falseCount = p.get(1).getAsDouble();
                    }
                    double total = trueCount + falseCount;
                    double percent = total > 0 ? (trueCount / total) * 100 : 0;
                    results.add(Map.entry(name, new double[]{percent, trueCount, trueCount + falseCount}));
                }

                // Sort highest to lowest
                results.sort(Comparator.comparingDouble(e -> -e.getValue()[0]));

                for (Map.Entry<String, double[]> entry : results) {
                    double percent = entry.getValue()[0];
                    int servers = (int) entry.getValue()[1];
                    int polled = (int) entry.getValue()[2];
                    int green = (int) Math.round(percent / 100.0 * 40);
                    int red = 40 - green;
                    String bar = "<#25FA07>" + "▇".repeat(green) + "<#424040>" + "▇".repeat(red);
                    sendColMsg(sender, "&7- &b%-25s &7[%s&7] &f%.1f%% &7(&b%d&7/&b%d&7)", entry.getKey(), bar, percent, servers, polled);
                }
            } catch (Exception e) {
                sendColMsg(sender, "&cFailed to fetch bStats data: &7" + e.getMessage());
                if (SkBee.isDebug()) e.printStackTrace();
            }
        });
    }

}
