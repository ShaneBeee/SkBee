package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.util.SkriptColor;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util class for translating BarColor to/from Skript Color
 */
public class BossBarUtils {

    private static final Map<Key, BossBar> BARS_BY_KEY = new HashMap<>();
    private static final Map<BossBar, @Nullable Key> KEYS_BY_BAR = new HashMap<>();

    public static SkriptColor getSkriptColor(BossBar.Color barColor) {
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

    public static BossBar.Color getBossBarColor(SkriptColor skriptColor) {
        return switch (skriptColor) {
            case DARK_GREY, LIGHT_GREY, WHITE -> BossBar.Color.WHITE;
            case DARK_BLUE, DARK_CYAN, LIGHT_CYAN -> BossBar.Color.BLUE;
            case DARK_GREEN, LIGHT_GREEN -> BossBar.Color.GREEN;
            case YELLOW, ORANGE -> BossBar.Color.YELLOW;
            case DARK_RED -> BossBar.Color.RED;
            case LIGHT_RED -> BossBar.Color.PINK;
            default -> BossBar.Color.PURPLE;
        };
    }

    public static @Nullable BossBar getByKey(Key key) {
        return BARS_BY_KEY.get(key);
    }

    public static @Nullable Key getKey(BossBar bossBar) {
        return KEYS_BY_BAR.get(bossBar);
    }

    public static List<BossBar> getAllBossBars() {
        return new ArrayList<>(KEYS_BY_BAR.keySet());
    }

    public static void removeBossBar(BossBar bossBar) {
        KEYS_BY_BAR.remove(bossBar);
        List<Key> keys = new ArrayList<>();
        BARS_BY_KEY.forEach((key, bossBar1) -> {
            if (bossBar1.equals(bossBar)) keys.add(key);
        });
        keys.forEach(BARS_BY_KEY::remove);
    }

    public static BossBar create(Key key, ComponentWrapper title, SkriptColor color, Overlay style, float progress) {
        BossBar bossBar = BossBar.bossBar(title.getComponent(), progress, getBossBarColor(color), style);
        BARS_BY_KEY.put(key, bossBar);
        KEYS_BY_BAR.put(bossBar, key);
        return bossBar;
    }

    public static BossBar create(ComponentWrapper title, SkriptColor color, Overlay style, float progress) {
        BossBar bossBar = BossBar.bossBar(title.getComponent(), progress, getBossBarColor(color), style);
        KEYS_BY_BAR.put(bossBar, null);
        return bossBar;
    }

}
