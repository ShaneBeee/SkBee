package com.shanebeestudios.skbee.api.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Consumer;

public class UpdateChecker implements Listener {

    private static String UPDATE_VERSION;

    public static void checkForUpdate(String pluginVersion) {
        Util.log("Checking for update...");
        if (pluginVersion.contains("-")) {
            Util.logLoading("&eYou're running a beta version, no need to check for an update!");
            return;
        }
        getVersion(version -> {
            if (version.equalsIgnoreCase(pluginVersion)) {
                Util.logLoading("&aPlugin is up to date!");
            } else {
                Util.logLoading("&cPlugin is not up to date!");
                Util.logLoading(" - Current version: &cv" + pluginVersion);
                Util.logLoading(" - Available update: &av" + version);
                UPDATE_VERSION = version;
            }
        });
    }

    private static void getVersion(final Consumer<String> consumer) {
        try {
            URL url = new URL("https://api.github.com/repos/ShaneBeee/SkBee/releases/latest");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            String tag_name = jsonObject.get("tag_name").getAsString();
            consumer.accept(tag_name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final SkBee PLUGIN;

    public UpdateChecker(SkBee plugin) {
        this.PLUGIN = plugin;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        if (UPDATE_VERSION == null) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("skbee.update.check")) return;

        Bukkit.getScheduler().runTaskLater(PLUGIN, bukkitTask -> {
            Util.sendColMsg(player, "&7[&bSk&3Bee&7] update available: &a" + UPDATE_VERSION);
            Util.sendColMsg(player, "&7[&bSk&3Bee&7] download at &bhttps://github.com/ShaneBeee/SkBee/releases");
        }, 60);
    }

}
