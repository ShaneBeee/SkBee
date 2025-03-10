package com.shanebeestudios.skbee.api.region.scheduler;

import com.shanebeestudios.skbee.api.region.scheduler.task.SpigotTask;
import com.shanebeestudios.skbee.api.region.scheduler.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

/**
 * A {@link Scheduler} using the {@link BukkitScheduler}
 */
public class SpigotScheduler implements Scheduler<BukkitTask> {

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    @Override
    public SpigotTask runTask(Runnable task) {
        return new SpigotTask(SCHEDULER.runTask(TaskUtils.getPlugin(), task));
    }

    @Override
    public SpigotTask runTaskAsync(Runnable task) {
        return new SpigotTask(SCHEDULER.runTaskAsynchronously(TaskUtils.getPlugin(), task));
    }

    @Override
    public SpigotTask runTaskLater(Runnable task, long delay) {
        return new SpigotTask(SCHEDULER.runTaskLater(TaskUtils.getPlugin(), task, delay));
    }

    @Override
    public Task<BukkitTask> runTaskLaterAsync(Runnable task, long delay) {
        return new SpigotTask(SCHEDULER.runTaskLaterAsynchronously(TaskUtils.getPlugin(), task, delay));
    }

    @Override
    public SpigotTask runTaskTimer(Runnable task, long delay, long period) {
        return new SpigotTask(SCHEDULER.runTaskTimer(TaskUtils.getPlugin(), task, delay, period));
    }

    @Override
    public Task<BukkitTask> runTaskTimerAsync(Runnable task, long delay, long period) {
        return new SpigotTask(SCHEDULER.runTaskTimerAsynchronously(TaskUtils.getPlugin(), task, delay, period));
    }

}
