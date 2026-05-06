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

import java.util.Locale;

/**
 * @hidden
 */
public class SkBeeMetrics {

    public static void loadMetrics(SkBee plugin, boolean enabled, Version skbeeVersion) { //6719
        Metrics metrics = new Metrics(plugin, 6719);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
        metrics.addCustomChart(new SimplePie("virtual_furnace", () -> String.valueOf(plugin.getPluginConfig().elements_virtual_furnace)));
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
        metrics.addCustomChart(new DrilldownPie("features_used", () -> {
            Table<String, String, Integer> table = HashBasedTable.create(1, 1);

            for (Features feature : Features.values()) {
                table.put(feature.getName(), feature.usedValue(), 1);
            }

            return table.rowMap();
        }));
    }

    public enum Features {
        ADVANCEMENTS,
        BOSSBARS,
        BOUNDS,
        DIALOGS,
        FASTBOARDS,
        GAME_EVENTS,
        ITEM_COMPONENTS,
        MINECRAFT_ENTITY,
        NBT,
        PARTICLES,
        RAYTRACE,
        RECIPE_EFFECTS,
        RECIPE_SECTIONS,
        REGISTRY,
        RUN_TASK,
        SCOREBOARDS,
        SCOREBOARD_OBJECTIVES,
        SCOREBOARD_TEAMS,
        STATISTICS,
        STRUCTURE_TEMPLATES,
        SWITCH_CASE,
        TAG_ALIASES,
        TEXT_COMPONENTS,
        WHILE_RUNNABLE,
        WORLD_CREATOR,
        WORLD_GEN;

        String usedValue = "false";

        private String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public void used() {
            this.usedValue = "true";
        }

        public void used(String value) {
            this.usedValue = value;
        }

        public String usedValue() {
            return this.usedValue;
        }
    }

}
