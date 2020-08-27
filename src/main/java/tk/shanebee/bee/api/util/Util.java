package tk.shanebee.bee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public class Util {

    private static final String PREFIX = "&7[&bSk&3Bee&7] ";
    private static final String PREFIX_ERROR = "&7[&bSk&3Bee &cERROR&7] ";

    public static String getColString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void log(String log) {
        Bukkit.getConsoleSender().sendMessage(getColString(PREFIX + log));
    }

    public static void skriptError(String error) {
        Skript.error(getColString(PREFIX_ERROR + error), ErrorQuality.SEMANTIC_ERROR);
    }

    /**
     * Convert a UUID to an int array
     * <p>Used for Minecraft 1.16+</p>
     *
     * @param uuid String UUID to convert
     * @return int array from UUID
     */
    public static int[] uuidToIntArray(String uuid) {
        return uuidToIntArray(UUID.fromString(uuid));
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
