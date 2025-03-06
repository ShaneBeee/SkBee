package com.shanebeestudios.skbee.api.scheduler;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.scheduler.task.FoliaTask;
import com.shanebeestudios.skbee.api.scheduler.task.Task;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.TimeUnit;

public class FoliaScheduler implements Scheduler<ScheduledTask> {

    private static final SkBee PLUGIN = SkBee.getPlugin();
    private static final GlobalRegionScheduler GLOBAL_SCHEDULER = Bukkit.getGlobalRegionScheduler();
    private static final RegionScheduler REGION_SCHEDULER = Bukkit.getRegionScheduler();
    private static final AsyncScheduler ASYNC_SCHEDULER = Bukkit.getAsyncScheduler();

    public static FoliaScheduler getGlobalScheduler() {
        return new FoliaScheduler();
    }

    public static FoliaScheduler getRegionalScheduler(Location location) {
        FoliaScheduler foliaScheduler = new FoliaScheduler();
        foliaScheduler.location = location;
        return foliaScheduler;
    }

    public static FoliaScheduler getEntityScheduler(Entity entity) {
        FoliaScheduler foliaScheduler = new FoliaScheduler();
        foliaScheduler.entity = entity;
        return foliaScheduler;
    }

    private Entity entity = null;
    private Location location = null;

    @Override
    public FoliaTask runTask(Runnable task) {
        ScheduledTask scheduledTask;
        if (this.entity != null) {
            scheduledTask = this.entity.getScheduler().run(PLUGIN, t -> task.run(), null);
        } else if (this.location != null) {
            scheduledTask = REGION_SCHEDULER.run(PLUGIN, this.location, t -> task.run());
        } else {
            scheduledTask = GLOBAL_SCHEDULER.run(PLUGIN, t -> task.run());
        }
        return new FoliaTask(scheduledTask);
    }

    @Override
    public FoliaTask runTaskAsync(Runnable task) {
        // TODO entity/location async stuff?!?!?
        ScheduledTask scheduledTask = ASYNC_SCHEDULER.runNow(PLUGIN, t -> task.run());
        return new FoliaTask(scheduledTask);
    }

    @Override
    public FoliaTask runTaskLater(Runnable task, long delay) {
        ScheduledTask scheduledTask;
        if (this.entity != null) {
            scheduledTask = this.entity.getScheduler().runDelayed(PLUGIN, t -> task.run(), null, delay);
        } else if (this.location != null) {
            scheduledTask = REGION_SCHEDULER.runDelayed(PLUGIN, this.location, t -> task.run(), delay);
        } else {
            scheduledTask = GLOBAL_SCHEDULER.runDelayed(PLUGIN, t -> task.run(), delay);
        }
        return new FoliaTask(scheduledTask);
    }

    @Override
    public Task<ScheduledTask> runTaskLaterAsync(Runnable task, long delay) {
        // TODO entity/location async stuff?!?!?
        ScheduledTask scheduledTask = ASYNC_SCHEDULER.runDelayed(PLUGIN, t -> task.run(), delay * 50, TimeUnit.MILLISECONDS);
        return new FoliaTask(scheduledTask);
    }

    @Override
    public FoliaTask runTaskTimer(Runnable task, long delay, long period) {
        ScheduledTask scheduledTask;
        if (this.entity != null) {
            scheduledTask = this.entity.getScheduler().runAtFixedRate(PLUGIN, t -> task.run(), null, delay, period);
        } else if (this.location != null) {
            scheduledTask = REGION_SCHEDULER.runAtFixedRate(PLUGIN, this.location, t -> task.run(), delay, period);
        } else {
            scheduledTask = GLOBAL_SCHEDULER.runAtFixedRate(PLUGIN, t -> task.run(), delay, period);
        }
        return new FoliaTask(scheduledTask);
    }

    @Override
    public Task<ScheduledTask> runTaskTimerAsync(Runnable task, long delay, long period) {
        // TODO entity/location async stuff?!?!?
        ScheduledTask scheduledTask = ASYNC_SCHEDULER.runAtFixedRate(PLUGIN, t -> task.run(),
            delay * 50, period * 50, TimeUnit.MILLISECONDS);
        return new FoliaTask(scheduledTask);
    }

}
