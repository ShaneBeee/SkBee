package com.shanebeestudios.skbee.api.region.scheduler.task;

/**
 * Base task to be implemented by server tasks
 *
 * @param <T> Task type
 */
public interface Task<T> {

    /**
     * Cancel this task
     */
    void cancel();

    /**
     * Check if this task has been canaclled
     *
     * @return Whether this task has been cancelled
     */
    boolean isCancelled();

    /**
     * Get the ID of this task
     *
     * @return ID of this task
     */
    int getTaskId();

}
