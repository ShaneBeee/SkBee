package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.util.SkriptColor;
import org.bukkit.boss.BarColor;

/**
 * Util class for translating BarColor <-> Skript Color
 */
public class BossBarUtils {

    public static SkriptColor getSkriptColor(BarColor barColor) {
        return switch (barColor) {
            case RED -> SkriptColor.DARK_RED;
            case YELLOW -> SkriptColor.YELLOW;
            case GREEN -> SkriptColor.DARK_GREEN;
            case BLUE -> SkriptColor.DARK_BLUE;
            case PINK -> SkriptColor.LIGHT_RED;
            case WHITE -> SkriptColor.WHITE;
            default -> SkriptColor.DARK_PURPLE;
        };
    }

    public static BarColor getBossBarColor(SkriptColor skriptColor) {
        return switch (skriptColor) {
            case DARK_GREY, LIGHT_GREY, WHITE -> BarColor.WHITE;
            case DARK_BLUE, DARK_CYAN, LIGHT_CYAN -> BarColor.BLUE;
            case DARK_GREEN, LIGHT_GREEN -> BarColor.GREEN;
            case YELLOW, ORANGE -> BarColor.YELLOW;
            case DARK_RED -> BarColor.RED;
            case LIGHT_RED -> BarColor.PINK;
            default -> BarColor.PURPLE;
        };
    }

}
