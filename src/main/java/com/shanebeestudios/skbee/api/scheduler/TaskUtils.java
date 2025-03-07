package com.shanebeestudios.skbee.api.scheduler;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Utility class for creating {@link Scheduler Schedulers}
 * <p>If running Spigot/Paper, will return a {@link SpigotScheduler}</p>
 * <p>If running Folia, will return a {@link FoliaScheduler}</p>
 */
public class TaskUtils {

    private static final boolean USE_PAPER_SCHEDULERS = Util.IS_RUNNING_FOLIA || SkBee.getPlugin().getPluginConfig().settings_use_paper_schedulers;

    /**
     * Get a global scheduler
     * <p>This is used for global World/Server tasks which don't require regions</p>
     * <p>If running Spigot or Paper (with Paper schedulers disabled) this will use a normal Bukkit Scheduler</p>
     *
     * @return Global scheduler
     */
    public static Scheduler<?> getGlobalScheduler() {
        if (USE_PAPER_SCHEDULERS) return FoliaScheduler.getGlobalScheduler();
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
        if (USE_PAPER_SCHEDULERS) return FoliaScheduler.getRegionalScheduler(location);
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
        if (USE_PAPER_SCHEDULERS) return FoliaScheduler.getEntityScheduler(entity);
        return new SpigotScheduler();
    }

    /**
     * Cancel all currently running tasks
     */
    public static void cancelTasks() {
        SkBee plugin = SkBee.getPlugin();
        if (USE_PAPER_SCHEDULERS) {
            Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
            Bukkit.getAsyncScheduler().cancelTasks(plugin);
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }

}
