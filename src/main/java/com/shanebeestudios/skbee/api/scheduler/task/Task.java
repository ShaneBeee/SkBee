package com.shanebeestudios.skbee.api.scheduler.task;

public interface Task<T> {

    void cancel();

    boolean isCancelled();

    int getTaskId();

}
