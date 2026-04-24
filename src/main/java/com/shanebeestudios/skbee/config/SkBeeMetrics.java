package com.shanebeestudios.skbee.config;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.shanebeestudios.skbee.SkBee;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;

public class SkBeeMetrics {

    public static void loadMetrics(SkBee plugin, boolean enabled, Version skbeeVersion) { //6719
        Metrics metrics = new Metrics(plugin, 6719);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
        metrics.addCustomChart(new SimplePie("virtual_furnace", () -> String.valueOf(plugin.getPluginConfig().ELEMENTS_VIRTUAL_FURNACE)));
        metrics.addCustomChart(new SimplePie("online_mode_proxy", () -> String.valueOf(Bukkit.getServerConfig().isProxyOnlineMode())));
        metrics.addCustomChart(new SimplePie("addon_loaded", () -> String.valueOf(enabled)));

        // New Metrics
        // Many of these are copied from Skript -> SkriptMetrics.class
        metrics.addCustomChart(new DrilldownPie("plugin_version_drilldown_pie", () -> {
            Version version = new Version(plugin.getPluginMeta().getVersion());
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);
            table.put(
                version.getMajor() + "." + version.getMinor() + ".x", // upper label
                version.toString(), // lower label
                1 // weight
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("skript_version_drilldown_pie", () -> {
            Version version = Skript.getVersion();
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);
            table.put(
                version.getMajor() + "." + version.getMinor() + ".x", // upper label
                version.toString(), // lower label
                1 // weight
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("minecraft_version_drilldown_pie", () -> {
            Version version = Skript.getMinecraftVersion();
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);

            if (version.getMajor() == 1) {
                // Minecraft 1.x.x versioning
                table.put(
                    version.getMajor() + "." + version.getMinor() + ".x", // upper label
                    version.toString(), // lower label
                    1 // weight
                );
            } else {
                // Minecraft (year).x.x versioning
                table.put(
                    version.getMajor() + ".x", // upper label
                    version.toString(), // lower label
                    1 // weight
                );
            }
            return table.rowMap();
        }));
        // Monitor Skript/Minecraft versions used per release of SkBee
        // This helps us understand which versions of Skript and Minecraft are most commonly used with SkBee per release
        metrics.addCustomChart(new DrilldownPie("skript_version_per_release_drilldown_pie", () -> {
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);
            Version skriptVersion = Skript.getVersion();

            table.put(
                skbeeVersion.getMajor() + "." + skbeeVersion.getMinor() + ".x",
                skriptVersion.toString(),
                1
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("minecraft_version_per_release_drilldown_pie", () -> {
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);
            Version minecraftVersion = Skript.getMinecraftVersion();

            table.put(
                skbeeVersion.getMajor() + "." + skbeeVersion.getMinor() + ".x",
                minecraftVersion.toString(),
                1
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("addon_loaded_per_release_drilldown_pie", () -> {
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);

            table.put(
                skbeeVersion.getMajor() + "." + skbeeVersion.getMinor() + ".x",
                String.valueOf(enabled),
                1
            );
            return table.rowMap();
        }));
    }

}
