package com.shanebeestudios.skbee.api.listener;

import ch.njol.skript.config.Config;
import ch.njol.skript.events.bukkit.PreScriptLoadEvent;
import com.google.common.io.Files;
import com.shanebeestudios.skbee.api.util.Util;
import org.apache.commons.text.StringEscapeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScriptListener implements Listener {

    private final List<String> DISABLED_SCRIPTS = new ArrayList<>();

    @EventHandler
    private void onCommand(ServerCommandEvent event) {
        processCommand(event, event.getSender(), event.getCommand());
    }

    @EventHandler
    private void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        processCommand(event, event.getPlayer(), event.getMessage());
    }

    private void processCommand(Cancellable event, CommandSender sender, String command) {
        command = command.toLowerCase(Locale.ROOT);
        if (!command.contains(" reload") && !command.contains(" enable") && !command.contains(" disable")) return;
        for (String disabled_script : DISABLED_SCRIPTS) {
            if (command.contains(disabled_script.toLowerCase())) {
                event.setCancelled(true);
                Util.sendColMsg(sender, "&cScript &7'&b%s&7' &chas been disabled by SkBee!", disabled_script);
            }
        }
    }

    private static final List<String> CHECK_STRINGS = new ArrayList<>();

    static {
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    private void onLoadScript(PreScriptLoadEvent event) {
        List<Config> scriptsToRemove = new ArrayList<>();
        for (Config script : event.getScripts()) {
            File scriptFile = script.getFile();
            assert scriptFile != null;
            String content;
            try {
                content = String.join("\n", Files.readLines(scriptFile, Charset.defaultCharset())).toLowerCase(Locale.ROOT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            CHECK_STRINGS.forEach(string -> {
                if (content.contains(StringEscapeUtils.unescapeJava(string).toLowerCase(Locale.ROOT))) {
                    scriptsToRemove.add(script);
                    String scriptName = scriptFile.getName().replace(".sk", "");
                    DISABLED_SCRIPTS.add(scriptName);
                }
            });
        }
        for (Config config : scriptsToRemove) {
            event.getScripts().remove(config);
        }
    }

}
