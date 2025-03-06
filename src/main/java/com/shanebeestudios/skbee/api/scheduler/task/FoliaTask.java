package com.shanebeestudios.skbee.api.scheduler.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaTask implements Task<ScheduledTask> {

    private final ScheduledTask scheduledTask;

    public FoliaTask(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    @Override
    public void cancel() {
        this.scheduledTask.cancel();
    }

}
