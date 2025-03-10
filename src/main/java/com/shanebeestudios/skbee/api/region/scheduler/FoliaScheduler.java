package com.shanebeestudios.skbee.api.region.scheduler;

import com.shanebeestudios.skbee.api.region.scheduler.task.FoliaTask;
import com.shanebeestudios.skbee.api.region.scheduler.task.Task;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * A {@link Scheduler} based on Folia/Paper regionalized schedulers
 */
public class FoliaScheduler implements Scheduler<ScheduledTask> {

    private static final GlobalRegionScheduler GLOBAL_SCHEDULER = Bukkit.getGlobalRegionScheduler();
    private static final RegionScheduler REGION_SCHEDULER = Bukkit.getRegionScheduler();
    private static final AsyncScheduler ASYNC_SCHEDULER = Bukkit.getAsyncScheduler();

    public static FoliaScheduler getGlobalScheduler() {
        return new FoliaScheduler(null, null);
    }

    public static FoliaScheduler getRegionalScheduler(Location location) {
        return new FoliaScheduler(null, location);
    }

    public static FoliaScheduler getEntityScheduler(Entity entity) {
        return new FoliaScheduler(entity, null);
    }

    private final @Nullable Entity entity;
    private final @Nullable Location location;

    public FoliaScheduler(@Nullable Entity entity, @Nullable Location location) {
        this.entity = entity;
        this.location = location;
    }

    @Override
    public FoliaTask runTask(Runnable task) {
        ScheduledTask scheduledTask;
        if (this.entity != null) {
            scheduledTask = this.entity.getScheduler().run(TaskUtils.getPlugin(), t -> task.run(), null);
        } else if (this.location != null) {
            scheduledTask = REGION_SCHEDULER.run(TaskUtils.getPlugin(), this.location, t -> task.run());
        } else {
            scheduledTask = GLOBAL_SCHEDULER.run(TaskUtils.getPlugin(), t -> task.run());
        }
        return new FoliaTask(scheduledTask);
    }

    @Override
    public FoliaTask runTaskAsync(Runnable task) {
        ScheduledTask scheduledTask = ASYNC_SCHEDULER.runNow(TaskUtils.getPlugin(), t -> task.run());
        return new FoliaTask(scheduledTask);
    }

    @Override
    public FoliaTask runTaskLater(Runnable task, long delay) {
        if (delay <= 0) delay = 1;
        ScheduledTask scheduledTask;
        if (this.entity != null) {
            scheduledTask = this.entity.getScheduler().runDelayed(TaskUtils.getPlugin(), t -> task.run(), null, delay);
        } else if (this.location != null) {
            scheduledTask = REGION_SCHEDULER.runDelayed(TaskUtils.getPlugin(), this.location, t -> task.run(), delay);
        } else {
            scheduledTask = GLOBAL_SCHEDULER.runDelayed(TaskUtils.getPlugin(), t -> task.run(), delay);
        }
        return new FoliaTask(scheduledTask);
    }

    @Override
    public Task<ScheduledTask> runTaskLaterAsync(Runnable task, long delay) {
        if (delay <= 0) delay = 1;
        ScheduledTask scheduledTask = ASYNC_SCHEDULER.runDelayed(TaskUtils.getPlugin(), t -> task.run(), delay * 50, TimeUnit.MILLISECONDS);
        return new FoliaTask(scheduledTask);
    }

    @Override
    public FoliaTask runTaskTimer(Runnable task, long delay, long period) {
        if (delay <= 0) delay = 1;
        ScheduledTask scheduledTask;
        if (this.entity != null) {
            scheduledTask = this.entity.getScheduler().runAtFixedRate(TaskUtils.getPlugin(), t -> task.run(), null, delay, period);
        } else if (this.location != null) {
            scheduledTask = REGION_SCHEDULER.runAtFixedRate(TaskUtils.getPlugin(), this.location, t -> task.run(), delay, period);
        } else {
            scheduledTask = GLOBAL_SCHEDULER.runAtFixedRate(TaskUtils.getPlugin(), t -> task.run(), delay, period);
        }
        return new FoliaTask(scheduledTask);
    }

    @Override
    public Task<ScheduledTask> runTaskTimerAsync(Runnable task, long delay, long period) {
        if (delay <= 0) delay = 1;
        ScheduledTask scheduledTask = ASYNC_SCHEDULER.runAtFixedRate(TaskUtils.getPlugin(), t -> task.run(),
            delay * 50, period * 50, TimeUnit.MILLISECONDS);
        return new FoliaTask(scheduledTask);
    }

}
