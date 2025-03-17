package com.shanebeestudios.skbee.api.region;

import com.shanebeestudios.skbee.api.region.scheduler.FoliaScheduler;
import com.shanebeestudios.skbee.api.region.scheduler.Scheduler;
import com.shanebeestudios.skbee.api.region.scheduler.SpigotScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for creating {@link Scheduler Schedulers}
 * <p>Intialize before use, {@link #initialize(Plugin, boolean)}</p>
 * <p>If initialized with `useFoliaSchedulers=true`, will return a {@link FoliaScheduler}
 * else will return a {@link SpigotScheduler}</p>
 */
public class TaskUtils {

    private static Plugin plugin;
    private static boolean useFoliaSchedulers;

    /**
     * Initialize schedulers
     *
     * @param plugin             Plugin to reference for tasks
     * @param useFoliaSchedulers Whether to use Paper/Folia schedulers else Bukkit schedulers
     */
    public static void initialize(@NotNull Plugin plugin, boolean useFoliaSchedulers) {
        if (TaskUtils.plugin != null) {
            throw new IllegalStateException("TaskUtils already initialized!");
        }
        TaskUtils.plugin = plugin;
        TaskUtils.useFoliaSchedulers = useFoliaSchedulers;
    }

    /**
     * Get the plugin that these schedulers will use
     *
     * @return Plugin for schedulers
     */
    public static Plugin getPlugin() {
        pluginCheck();
        return plugin;
    }

    /**
     * Get a global scheduler
     * <p>This is used for global World/Server tasks which don't require regions</p>
     * <p>If running Spigot or Paper (with Paper schedulers disabled) this will use a normal Bukkit Scheduler</p>
     *
     * @return Global scheduler
     */
    public static Scheduler<?> getGlobalScheduler() {
        pluginCheck();
        if (useFoliaSchedulers) return FoliaScheduler.getGlobalScheduler();
        return new SpigotScheduler();
    }

    /**
     * Get a regional scheduler based on a location
     * <p>This is used for scheduling tasks at a specific location</p>
     * <p>If running Spigot or Paper (with Paper schedulers disabled) this will use a normal Bukkit Scheduler</p>
     *
     * @param location Location to grab region from
     * @return Region scheduler
     */
    public static Scheduler<?> getRegionalScheduler(Location location) {
        pluginCheck();
        if (useFoliaSchedulers) return FoliaScheduler.getRegionalScheduler(location);
        return new SpigotScheduler();
    }

    /**
     * Get an entity scheduler
     * <p>This is used for scheduling tasks linked to an entity
     * The tasks will move with the entity to whatever region they're in</p>
     * <p>If running Spigot or Paper (with Paper schedulers disabled) this will use a normal Bukkit Scheduler</p>
     *
     * @param entity Entity to attach scheduler to
     * @return Entity scheduler
     */
    public static Scheduler<?> getEntityScheduler(Entity entity) {
        pluginCheck();
        if (useFoliaSchedulers) return FoliaScheduler.getEntityScheduler(entity);
        return new SpigotScheduler();
    }

    /**
     * Cancel all currently running tasks
     */
    public static void cancelTasks() {
        pluginCheck();
        if (useFoliaSchedulers) {
            Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
            Bukkit.getAsyncScheduler().cancelTasks(plugin);
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }

    private static void pluginCheck() {
        if (plugin == null) {
            throw new IllegalStateException("TaskUtils has not been initialized!");
        }
    }

}
