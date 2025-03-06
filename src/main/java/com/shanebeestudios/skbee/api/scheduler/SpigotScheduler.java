package com.shanebeestudios.skbee.api.scheduler;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.scheduler.task.SpigotTask;
import com.shanebeestudios.skbee.api.scheduler.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class SpigotScheduler implements Scheduler<BukkitTask> {

    private static final SkBee PLUGIN = SkBee.getPlugin();
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    @Override
    public SpigotTask runTask(Runnable task) {
        return new SpigotTask(SCHEDULER.runTask(PLUGIN, task));
    }

    @Override
    public SpigotTask runTaskAsync(Runnable task) {
        return new SpigotTask(SCHEDULER.runTaskAsynchronously(PLUGIN, task));
    }

    @Override
    public SpigotTask runTaskLater(Runnable task, long delay) {
        return new SpigotTask(SCHEDULER.runTaskLater(PLUGIN, task, delay));
    }

    @Override
    public Task<BukkitTask> runTaskLaterAsync(Runnable task, long delay) {
        return new SpigotTask(SCHEDULER.runTaskLaterAsynchronously(PLUGIN, task, delay));
    }

    @Override
    public SpigotTask runTaskTimer(Runnable task, long delay, long period) {
        return new SpigotTask(SCHEDULER.runTaskTimer(PLUGIN, task, delay, period));
    }

    @Override
    public Task<BukkitTask> runTaskTimerAsync(Runnable task, long delay, long period) {
        return new SpigotTask(SCHEDULER.runTaskTimerAsynchronously(PLUGIN, task, delay, period));
    }

}
