package com.jt.server.task;


/**
 * The {@link ScheduledTask} Trigger Intervall.
 * 
 * @author Hendrik Stein
 * 
 */
public class Trigger {

    /** The trigger time unit. */
    private TriggerTimeUnit timeUnit;

    /** The trigger interval. */
    private long interval;

    /**
     * Create an instance.
     * 
     * @param timeUnit the trigger time unit
     * @param interval the trigger interval
     */
    public Trigger(TriggerTimeUnit timeUnit, long interval) {
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    /**
     * Get the trigger interval {@link TriggerTimeUnit}
     * 
     * @return the trigger interval timeunit
     */
    public TriggerTimeUnit getTriggerTimeUnit() {
        return timeUnit;
    }

    /**
     * Get the trigger interval.
     * 
     * @return the trigger interval
     */
    public long getInterval() {
        return interval;
    }
}
