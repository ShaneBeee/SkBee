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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Utility class to check for plugin updates
 */
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
                Util.logLoading(" - Current version: &cv%s", pluginVersion);
                Util.logLoading(" - Available update: &av%s",version);
                Util.logLoading(" - Download available at: https://github.com/ShaneBeee/SkBee/releases");
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
            if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
                e.printStackTrace();
            } else {
                Util.logLoading("&cChecking for update failed!");
            }
        }
    }

    private final SkBee plugin;

    public UpdateChecker(SkBee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("skbee.update.check")) return;

        String currentVersion = this.plugin.getDescription().getVersion();
        CompletableFuture<String> updateVersion = getUpdateVersion(currentVersion);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> updateVersion.thenApply(version -> {
            Util.sendColMsg(player, "&7[&bSk&3Bee&7] update available: &a" + version);
            Util.sendColMsg(player, "&7[&bSk&3Bee&7] download at &bhttps://github.com/ShaneBeee/SkBee/releases");
            return true;
        }), 30);
    }

    private CompletableFuture<String> getUpdateVersion(String currentVersion) {
        CompletableFuture<String> future = new CompletableFuture<>();
        if (UPDATE_VERSION != null) {
            future.complete(UPDATE_VERSION);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> getVersion(version -> {
                if (!version.equalsIgnoreCase(currentVersion)) {
                    UPDATE_VERSION = currentVersion;
                    future.complete(version);
                } else {
                    future.cancel(true);
                }
            }));
        }
        return future;
    }

}
