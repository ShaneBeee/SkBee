package com.shanebeestudios.skbee.api.scheduler;

import com.shanebeestudios.skbee.api.scheduler.task.Task;

/**
 * Scheduler for scheduling tasks
 * <p>Changes based on Spigot/Paper vs. Folia usage</p>
 *
 * @param <T> Underlying task return type
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Scheduler<T> {

    /**
     * Run a task on the next tick
     *
     * @param task Task to run
     * @return Instance of scheduler
     */
    Task<T> runTask(Runnable task);

    /**
     * Run an async task on the next tick
     *
     * @param task Task to run
     * @return Instance of scheduler
     */
    Task<T> runTaskAsync(Runnable task);

    /**
     * Run a task at a later time
     *
     * @param task  Task to run
     * @param delay Delay in ticks
     * @return Instance of scheduler
     */
    Task<T> runTaskLater(Runnable task, long delay);

    /**
     * Run an async task at a later time
     *
     * @param task  Task to run
     * @param delay Delay in ticks
     * @return Instance of scheduler
     */
    Task<T> runTaskLaterAsync(Runnable task, long delay);

    /**
     * Run a repeating task
     *
     * @param task   Task to run
     * @param delay  Delay in ticks to start
     * @param period Period in ticks to repeat
     * @return Instance of scheduler
     */
    Task<T> runTaskTimer(Runnable task, long delay, long period);

    /**
     * Run an async repeating task
     *
     * @param task   Task to run
     * @param delay  Delay in ticks to start
     * @param period Period in ticks to repeat
     * @return Instance of scheduler
     */
    Task<T> runTaskTimerAsync(Runnable task, long delay, long period);

}
