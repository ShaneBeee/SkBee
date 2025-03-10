package com.shanebeestudios.skbee.api.region.scheduler.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

/**
 * Task wrapper for Folia's {@link ScheduledTask}
 */
public class FoliaTask implements Task<ScheduledTask> {

    private final ScheduledTask scheduledTask;

    public FoliaTask(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    @Override
    public void cancel() {
        this.scheduledTask.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.scheduledTask.isCancelled();
    }

    @Override
    public int getTaskId() {
        // ScheduledTask doesn't have an ID?!?!?
        return -1;
    }

}
