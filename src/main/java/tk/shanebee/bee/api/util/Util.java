package tk.shanebee.bee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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

}
