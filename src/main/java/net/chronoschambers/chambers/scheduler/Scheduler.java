package net.chronoschambers.chambers.scheduler;

import org.jetbrains.annotations.NotNull;

/**
 * Simple abstraction for scheduling synchronous and asynchronous tasks.
 */
public interface Scheduler {

    /**
     * Runs a task asynchronously.
     *
     * @param runnable the task to execute
     */
    void async(@NotNull Runnable runnable);

    /**
     * Runs a task on the main (synchronous) thread.
     *
     * @param runnable the task to execute
     */
    void sync(@NotNull Runnable runnable);

}
