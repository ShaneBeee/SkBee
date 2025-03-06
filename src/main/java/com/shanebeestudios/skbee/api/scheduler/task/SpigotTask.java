package com.shanebeestudios.skbee.api.scheduler.task;

import org.bukkit.scheduler.BukkitTask;

public class SpigotTask implements Task<BukkitTask> {

    private final BukkitTask bukkitTask;

    public SpigotTask(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    @Override
    public void cancel() {
        this.bukkitTask.cancel();
    }

}
