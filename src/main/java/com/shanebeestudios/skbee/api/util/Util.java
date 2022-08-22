package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;
import com.shanebeestudios.skbee.SkBee;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final String PREFIX = "&7[&bSk&3Bee&7] ";
    private static final String PREFIX_ERROR = "&7[&bSk&3Bee &cERROR&7] ";
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f\\d]){6}>");
    private static final boolean SKRIPT_IS_THERE = SkBee.getPlugin().getSkriptPlugin() != null;

    @SuppressWarnings("deprecation") // Paper deprecation
    public static String getColString(String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        if (SKRIPT_IS_THERE) {
            while (matcher.find()) {
                final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
                final String before = string.substring(0, matcher.start());
                final String after = string.substring(matcher.end());
                string = before + hexColor + after;
                matcher = HEX_PATTERN.matcher(string);
            }
        } else {
            string = HEX_PATTERN.matcher(string).replaceAll("");
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void sendColMsg(CommandSender receiver, String format, Object... objects) {
        receiver.sendMessage(getColString(String.format(format, objects)));
    }

    public static void log(String format, Object... objects) {
        String log = String.format(format, objects);
        Bukkit.getConsoleSender().sendMessage(getColString(PREFIX + log));
    }

    public static void skriptError(String format, Object... objects) {
        String error = String.format(format, objects);
        Skript.error(getColString(PREFIX_ERROR + error), ErrorQuality.SEMANTIC_ERROR);
    }

    public static void debug(String format, Object... objects) {
        if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
            String debug = String.format(format, objects);
            Bukkit.getConsoleSender().sendMessage(getColString(PREFIX_ERROR + debug));
        }
    }

    private static final List<String> DEBUGS = new ArrayList<>();

    public static void logLoading(String format, Object... objects) {
        String form = String.format(format, objects);
        DEBUGS.add(form);
        log(form);
    }

    public static List<String> getDebugs() {
        return DEBUGS;
    }

    public static NamespacedKey getNamespacedKey(@NotNull String key, boolean error) {
        if (key.contains(" ")) {
            key = key.replace(" ", "_");
        }
        key = key.toLowerCase(Locale.ROOT);
        if (key.contains(":")) {
            NamespacedKey namespacedKey = NamespacedKey.fromString(key);
            if (namespacedKey == null) {
                if (error) {
                    skriptError("Invalid key. Must be [a-z0-9/._-:]: %s", key);
                }
                return null;
            }
            return namespacedKey;
        } else {
            try {
                return new NamespacedKey(SkBee.getPlugin(), key);
            } catch (IllegalArgumentException ex) {
                if (error) {
                    skriptError(ex.getMessage());
                }
                return null;
            }
        }
    }

    /**
     * Convert a UUID to an int array
     * <p>Used for Minecraft 1.16+</p>
     *
     * @param uuid UUID to convert
     * @return int array from UUID
     */
    public static int[] uuidToIntArray(UUID uuid) {
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        return new int[]{(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }

}
