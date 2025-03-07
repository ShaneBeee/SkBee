package com.shanebeestudios.skbee.api.scheduler.task;

import org.bukkit.scheduler.BukkitTask;

/**
 * Task wrapper for Bukkit's {@link BukkitTask}
 */
public class SpigotTask implements Task<BukkitTask> {

    private final BukkitTask bukkitTask;

    public SpigotTask(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    @Override
    public void cancel() {
        this.bukkitTask.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.bukkitTask.isCancelled();
    }

    @Override
    public int getTaskId() {
        return this.bukkitTask.getTaskId();
    }

}
