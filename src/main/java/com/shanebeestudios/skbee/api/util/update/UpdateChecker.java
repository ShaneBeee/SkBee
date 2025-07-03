package com.shanebeestudios.skbee.api.util.update;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
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

/**
 * Utility class to check for plugin updates
 */
@SuppressWarnings("deprecation")
public class UpdateChecker implements Listener {

    private final SkBee plugin;
    private final Version pluginVersion;
    private final Version serverVersion = Skript.getMinecraftVersion();
    private ModrinthVersion currentUpdateVersion;

    public UpdateChecker(SkBee plugin) {
        this.plugin = plugin;
        this.pluginVersion = new Version(plugin.getDescription().getVersion());

        Config config = plugin.getPluginConfig();
        if (config.SETTINGS_UPDATE_CHECKER_ENABLED) {
            setupJoinListener();
            checkUpdate(config.SETTINGS_UPDATE_CHECKER_ASYNC);
        } else {
            Util.log("&3Update checker disabled!");
        }
    }

    private void setupJoinListener() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            private void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                if (!player.hasPermission("skbee.update.check")) return;

                TaskUtils.getEntityScheduler(player).runTaskLater(() -> getUpdateVersion(true).thenApply(version -> {
                    Util.sendColMsg(player, "&7[&bSk&3Bee&7] Update available: &a" + version.getUpdateVersion());
                    Util.sendColMsg(player, "&7[&bSk&3Bee&7] Download at: &b" + version.getUpdateLink());
                    return true;
                }), 30);
            }
        }, this.plugin);
    }

    private void checkUpdate(boolean async) {
        Util.log("Checking for update...");
        getUpdateVersion(async).thenApply(modrinthVersion -> {
            Util.logLoading("&cPlugin is not up to date!");
            Util.logLoading(" - Current version: &cv%s", this.pluginVersion);
            Util.logLoading(" - Available update: &av%s", modrinthVersion.getUpdateVersion());
            if (modrinthVersion.isServerSupported(this.serverVersion)) {
                Util.logLoading(" - Download at: &b" + modrinthVersion.getUpdateLink());
            } else {
                Util.logLoading(" - &cYour server version &7(&e%s&7) &cdoes not support this update.", this.serverVersion);
                Util.logLoading(" - Supported Versions:");
                for (Version supportedVersion : modrinthVersion.getSupportedVersions()) {
                    Util.logLoading("   - " + supportedVersion.toString());
                }
            }
            return true;
        }).exceptionally(throwable -> {
            Util.logLoading("&aPlugin is up to date!");
            return true;
        });
    }

    private CompletableFuture<ModrinthVersion> getUpdateVersion(boolean async) {
        CompletableFuture<ModrinthVersion> updateVersionFuture = new CompletableFuture<>();
        if (this.currentUpdateVersion != null) {
            updateVersionFuture.complete(this.currentUpdateVersion);
        } else {
            CompletableFuture<ModrinthVersion> latestReleaseFuture = new CompletableFuture<>();
            if (async) {
                TaskUtils.getGlobalScheduler().runTaskAsync(() -> {
                    ModrinthVersion lastest = getLatestVersionFromModrinth();
                    if (lastest == null) latestReleaseFuture.cancel(true);
                    latestReleaseFuture.complete(lastest);
                });
            } else {
                ModrinthVersion latest = getLatestVersionFromModrinth();
                if (latest == null) latestReleaseFuture.cancel(true);
                latestReleaseFuture.complete(latest);
            }
            latestReleaseFuture.thenApply(version -> {
                if (version.getUpdateVersion().compareTo(this.pluginVersion) <= 0) {
                    updateVersionFuture.cancel(true);
                } else {
                    this.currentUpdateVersion = version;
                    updateVersionFuture.complete(this.currentUpdateVersion);
                }
                return true;
            });
        }
        return updateVersionFuture;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private ModrinthVersion getLatestVersionFromModrinth() {
        try {
            URL url = new URL("https://api.modrinth.com/v2/project/a0tlbHZO/version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonArray elements = new Gson().fromJson(reader, JsonArray.class);
            JsonElement latestVersion = elements.get(0);
            return new ModrinthVersion(latestVersion);
        } catch (IOException e) {
            if (SkBee.isDebug()) {
                e.printStackTrace();
            } else {
                Util.logLoading("&cChecking for update failed!");
            }
        }
        return null;
    }

}
