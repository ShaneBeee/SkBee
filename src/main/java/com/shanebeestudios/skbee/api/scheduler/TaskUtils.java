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

    public static Scheduler<?> getGlobalScheduler() {
        if (Util.IS_RUNNING_FOLIA) return FoliaScheduler.getGlobalScheduler();
        return new SpigotScheduler();
    }

    public static Scheduler<?> getRegionalScheduler(Location location) {
        if (Util.IS_RUNNING_FOLIA) return FoliaScheduler.getRegionalScheduler(location);
        return new SpigotScheduler();
    }

    public static Scheduler<?> getEntityScheduler(Entity entity) {
        if (Util.IS_RUNNING_FOLIA) return FoliaScheduler.getEntityScheduler(entity);
        return new SpigotScheduler();
    }

    /**
     * Cancel all currently running tasks
     */
    public static void cancelTasks() {
        SkBee plugin = SkBee.getPlugin();
        if (Util.IS_RUNNING_FOLIA) {
            Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
            Bukkit.getAsyncScheduler().cancelTasks(plugin);
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }

}
