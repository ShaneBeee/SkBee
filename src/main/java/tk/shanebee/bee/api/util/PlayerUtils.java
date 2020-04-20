package tk.shanebee.bee.api.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tk.shanebee.bee.api.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class PlayerUtils {

    public static int getXPAtLevel(int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        }
        if (level <= 30) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;
    }

    public static int getTotalXP(Player player) {
        int exp = Math.round(getXPAtLevel(player.getLevel()) * player.getExp());
        int currentLevel = player.getLevel();
        while (currentLevel > 0) {
            currentLevel--;
            exp += getXPAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = 0;
        }
        return exp;
    }

    public static void setTotalXP(Player player, int level) {
        player.setLevel(0);
        player.setExp(0);
        player.giveExp(level);
    }

    /**
     * Disable a player's coordinates in their Minecraft Debug screen
     *
     * @param player The player to disable coords for
     */
    @SuppressWarnings("WeakerAccess")
    public static void disableF3(Player player) {
        try {
            Class<?> packetClass = ReflectionUtils.getNMSClass("PacketPlayOutEntityStatus");
            Constructor<?> packetConstructor = packetClass.getConstructor(ReflectionUtils.getNMSClass("Entity"), Byte.TYPE);
            Object packet = packetConstructor.newInstance(ReflectionUtils.getHandle(player), (byte) 22);
            Method sendPacket = ReflectionUtils.getNMSClass("PlayerConnection").getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet"));
            sendPacket.invoke(ReflectionUtils.getConnection(player), packet);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("[SurvivalPlus] " + ChatColor.RED + e.getMessage());
        }
    }

    /**
     * Enable a player's coordinates in their Minecraft Debug screen
     *
     * @param player The player to enable coords for
     */
    @SuppressWarnings("WeakerAccess")
    public static void enableF3(Player player) {
        try {
            Class<?> packetClass = ReflectionUtils.getNMSClass("PacketPlayOutEntityStatus");
            Constructor<?> packetConstructor = packetClass.getConstructor(ReflectionUtils.getNMSClass("Entity"), Byte.TYPE);
            Object packet = packetConstructor.newInstance(ReflectionUtils.getHandle(player), (byte) 23);
            Method sendPacket = ReflectionUtils.getNMSClass("PlayerConnection").getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet"));
            sendPacket.invoke(ReflectionUtils.getConnection(player), packet);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("[SurvivalPlus] " + ChatColor.RED + e.getMessage());
        }
    }

}
