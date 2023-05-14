package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;
import com.shanebeestudios.skbee.SkBee;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General utility class
 */
public class Util {

    private static final String PREFIX = "&7[&bSk&3Bee&7] ";
    private static final String PREFIX_ERROR = "&7[&bSk&3Bee &cERROR&7] ";
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f\\d]){6}>");
    private static final boolean SKRIPT_IS_THERE = Bukkit.getPluginManager().getPlugin("Skript") != null;
    private static final String SETTINGS_NAMESPACE = SkBee.getPlugin().getPluginConfig().SETTINGS_NAMESPACE;

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

    /**
     * Gets a Minecraft NamespacedKey from string
     * <p>If no colon (':') is not include it will prepend it to the start</p>
     *
     * @param key   Key for new Minecraft NamespacedKey
     * @param error Whether to send a skript/console error if one occurs
     * @return new Minecraft NamespacedKey
     */
    @Nullable
    public static NamespacedKey getMCNamespacedKey(@NotNull String key, boolean error) {
        if (!key.contains(":")) key = "minecraft:" + key;
        return getNamespacedKey(key, error);
    }

    /**
     * Get a NamespacedKey from string
     * <p>If no namespace is provided, it will default to namespace in SkBee config (default = "skbee")</p>
     *
     * @param key   Key for new NamespacedKey, ex: "plugin:key" or "minecraft:something"
     * @param error Whether to send a skript/console error if one occurs
     * @return new NamespacedKey
     */
    @Nullable
    public static NamespacedKey getNamespacedKey(@NotNull String key, boolean error) {
        key = key.toLowerCase();
        if (key.contains(" ")) {
            key = key.replace(" ", "_");
        }

        NamespacedKey namespacedKey = null;
        if (key.contains(":")) {
            namespacedKey = NamespacedKey.fromString(key);
        } else if (SETTINGS_NAMESPACE != null) {
            namespacedKey = new NamespacedKey(SETTINGS_NAMESPACE, key);
        } else { // Just a safety check, settings_namespace can't be null but in case this is defaulted to.
            try {
                namespacedKey = new NamespacedKey(SkBee.getPlugin(), key);
            } catch (Exception exception) {
                if (error) {
                    skriptError(exception.getMessage());
                }
            }
        }
        if (namespacedKey == null && error)
            skriptError("An invalid key was provided, that didn't follow [a-z0-9/._-:]. key: %s", key);
        return namespacedKey;
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
