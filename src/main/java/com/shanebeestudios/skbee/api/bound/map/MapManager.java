package com.shanebeestudios.skbee.api.bound.map;

import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

/**
 * Manager for Map Plugins
 */
public class MapManager {

    @Nullable
    private static final Map MAP_API;

    static {
        if (Bukkit.getPluginManager().getPlugin("BlueMap") != null) {
            MAP_API = new BlueMapMap();
        } else {
            MAP_API = null;
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
