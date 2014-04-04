package com.jt.server.task;

/**
 * Base Schedule Task.
 * 
 * @author Hendrik Stein
 * 
 */
public abstract class ScheduledTask implements Runnable {
    /** The scheduled task name. */
    private String name;

    /** The trigger. */
    private Trigger trigger;

    /**
     * Create an instance
     * 
     * @param name the scheduled task name
     * @param trigger the trigger
     */
    public ScheduledTask(String name, Trigger trigger) {
        this.name = name;
        this.trigger = trigger;
    }

    /**
     * Get the name of the scheduled task
     * 
     * @return the tasks name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the {@link Trigger} of the scheduled task
     * 
     * @return the trigger
     */
    public Trigger getTrigger() {
        return trigger;
    }
}
