package com.shanebeestudios.skbee.api.bound;

import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public class BoundUtils {

    public static String serializeColor(Color color) {
        return color.getRed() + ":" + color.getGreen() + ":" + color.getBlue();
    }

    @NotNull
    public static Color deserializeColor(String string) {
        String[] split = string.split(":");
        if (split.length == 3) {
            try {
                int r = Integer.parseInt(split[0]);
                int g = Integer.parseInt(split[1]);
                int b = Integer.parseInt(split[2]);
                return Color.fromRGB(r, g, b);
            } catch (NumberFormatException ex) {
                if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
                    ex.printStackTrace();
                }
            }

        }
        return Color.fromRGB(255, 0, 0);
    }
}
