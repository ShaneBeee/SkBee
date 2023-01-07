package com.shanebeestudios.skbee.api.bound.map;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

/**
 * Manager for Map Plugins
 */
public class MapManager {

    @Nullable
    private static MapApi MAP_API = null;

    static {
        if (SkBee.getPlugin().getPluginConfig().ELEMENTS_BOUND_MARKERS) {
            if (Bukkit.getPluginManager().getPlugin("BlueMap") != null) {
                MAP_API = new BlueMapMapApi();
            }
        }
    }

    public static void addMarker(Bound bound) {
        if (MAP_API != null) {
            MAP_API.addMarker(bound);
        }
    }

    public static void removeMarker(Bound bound) {
        if (MAP_API != null) {
            MAP_API.removeMarker(bound);
        }
    }

    public static void setLabel(Bound bound, String label) {
        if (MAP_API != null) {
            MAP_API.setLabel(bound, label);
        }
    }

    public static void setMarkerLineColor(Bound bound, Color color) {
        if (MAP_API != null) {
            MAP_API.setMarkerLineColor(bound, color);
        }
    }

    public static void setMarkerFillColor(Bound bound, Color color) {
        if (MAP_API != null) {
            MAP_API.setMarkerFillColor(bound, color);
        }
    }

}
