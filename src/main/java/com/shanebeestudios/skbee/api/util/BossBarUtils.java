package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.util.SkriptColor;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for translating BarColor to/from Skript Color
 */
public class BossBarUtils {

    /**
     * Convert a BarColor to a Skript Color
     *
     * @param barColor BarColor to convert
     * @return SkriptColor representation of the BarColor
     */
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

    /**
     * Convert a Skript Color to a BarColor
     *
     * @param skriptColor SkriptColor to convert
     * @return BarColor representation of the SkriptColor
     */
    public static BarColor getBossBarColor(@Nullable SkriptColor skriptColor) {
        if (skriptColor == null) return BarColor.PURPLE;
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
