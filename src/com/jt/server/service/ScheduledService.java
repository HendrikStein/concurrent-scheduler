package com.jt.server.service;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.jt.server.task.ScheduledTask;
import com.jt.server.task.Trigger;
import com.jt.server.task.TriggerTimeUnit;

/**
 * Scheduled Task service.
 * 
 * @author Hendrik Stein
 * 
 */
public class ScheduledService {
    /** Scheduler executor service. */
    private static ScheduledExecutorService scheduledExecutorService;

    private ScheduledService() {
        // Static access only
    }

    /**
     * Schedule a {@link ScheduledTask} cluster wide.
     * 
     * @param scheduledTask the scheduled task
     */
    public static void scheduleTask(ScheduledTask scheduledTask) {
        Trigger trigger = scheduledTask.getTrigger();
        getInstance().scheduleAtFixedRate(scheduledTask, 0, trigger.getInterval(),
                TimeUnit.valueOf(trigger.getTriggerTimeUnit().toString()));
    }

    /**
     * Is this scheduled task already executed by another cluster node or is this thread able to request the cluster
     * wide lock.
     * 
     * @param scheduledTask the task to schedule
     * @return true if
     */
    public static boolean isRequestLockAllowed(ScheduledTask scheduledTask) {
        Date lookupExecutionDate = HazelcastService.getExecutionDateForTask(scheduledTask.getName());
        if (lookupExecutionDate != null) {
            TriggerTimeUnit timeUnit = scheduledTask.getTrigger().getTriggerTimeUnit();
            long period = scheduledTask.getTrigger().getInterval();
            Duration difference = new Duration(new DateTime(lookupExecutionDate), DateTime.now());
            switch (timeUnit) {
            case DAYS:
                if (difference.toStandardDays().getDays() > period) {
                    return true;
                }
                break;
            case HOURS:
                if (difference.toStandardHours().getHours() > period) {
                    return true;
                }
            case MINUTES:
                if (difference.toStandardMinutes().getMinutes() > period) {
                    return true;
                }
            case SECONDS:
                if (difference.toStandardSeconds().getSeconds() > period) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Shutdown scheduled executor by now and ignore the list of waiting tasks.
     */
    public static void shutdownNow() {
        getInstance().shutdownNow();
    }

    /**
     * Returns the scheduler executor instance.
     * 
     * @return the ScheduledExecutorService
     */
    private synchronized static ScheduledExecutorService getInstance() {
        if (scheduledExecutorService == null) {
            /** Create Threadpool with 5 Worker threads. */
            scheduledExecutorService = Executors.newScheduledThreadPool(5);
        }
        return scheduledExecutorService;
    }
}