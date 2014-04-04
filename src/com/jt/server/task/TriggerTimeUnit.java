package com.jt.server.task;

/**
 * 
 * @author Hendrik Stein
 * 
 */
public enum TriggerTimeUnit {
    DAYS,
    HOURS,
    MINUTES,
    SECONDS;

    /**
     * Get instance for value
     * 
     * @param key the instance key
     * @return the TriggerTimeUnit instance
     * @throws IllegalArgumentException if key not found
     */
    public static TriggerTimeUnit getInstance(String key) throws IllegalArgumentException {
        for (TriggerTimeUnit unit : values()) {
            if (unit.toString().equalsIgnoreCase(key)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Value " + key + " not found");
    }
}
