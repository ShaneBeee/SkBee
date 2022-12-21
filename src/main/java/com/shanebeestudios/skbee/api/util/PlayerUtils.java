package com.shanebeestudios.skbee.api.util;

import org.bukkit.entity.Player;

/**
 * Utility class for {@link Player Players}
 */
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
        if (level > 0) {
            player.giveExp(level);
        }
    }

}
