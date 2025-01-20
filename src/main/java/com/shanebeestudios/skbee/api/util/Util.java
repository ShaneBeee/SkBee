package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.Version;
import com.shanebeestudios.skbee.SkBee;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    // QuickLinks
    public static final String MCWIKI_TICK_COMMAND = "See [**Tick Command**](https://minecraft.wiki/w/Commands/tick) on McWiki for more details.";

    // Shortcut for finding stuff to remove later
    public static final boolean IS_RUNNING_SKRIPT_2_10 = Skript.getVersion().isLargerThan(new Version(2, 9, 999));
    public static final boolean IS_RUNNING_MC_1_21 = Skript.isRunningMinecraft(1, 21);

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

    /**
     * Send an error to admins
     * <p>Permission required: `skbee.admin`</p>
     *
     * @param format  Format of the message
     * @param objects Objects to fill format
     */
    public static void errorForAdmins(String format, Object... objects) {
        String error = PREFIX_ERROR + "&c" + format;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("skbee.admin")) {
                sendColMsg(player, error, objects);
            }
        }
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
     * <p>If a namespace is not provided, it will default to "minecraft:" namespace</p>
     *
     * @param key   Key for new Minecraft NamespacedKey
     * @param error Whether to send a skript/console error if one occurs
     * @return new Minecraft NamespacedKey
     */
    @Nullable
    public static NamespacedKey getNamespacedKey(@Nullable String key, boolean error) {
        if (key == null) return null;
        if (!key.contains(":")) key = "minecraft:" + key;
        if (key.length() > 255) {
            if (error)
                skriptError("An invalid key was provided, key must be less than 256 characters: %s", key);
            return null;
        }
        key = key.toLowerCase();
        if (key.contains(" ")) {
            key = key.replace(" ", "_");
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(key);
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

    /**
     * Check if a {@link Tag} is a {@link Material} Tag
     *
     * @param tag tag to check
     * @return True if material tag
     */
    public static boolean isMaterialTag(Tag<?> tag) {
        ParameterizedType superC = (ParameterizedType) tag.getClass().getGenericSuperclass();
        for (Type arg : superC.getActualTypeArguments()) {
            if (arg.equals(Material.class)) return true;
        }
        return false;
    }

    /**
     * Check if a {@link Tag} is an {@link EntityType} Tag
     *
     * @param tag tag to check
     * @return True if EntityType tag
     */
    public static boolean isEntityTypeTag(Tag<?> tag) {
        ParameterizedType superC = (ParameterizedType) tag.getClass().getGenericSuperclass();
        for (Type arg : superC.getActualTypeArguments()) {
            if (arg.equals(EntityType.class)) return true;
        }
        return false;
    }

}
