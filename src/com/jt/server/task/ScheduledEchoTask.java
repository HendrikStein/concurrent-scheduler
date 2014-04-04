package com.jt.server.task;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ILock;
import com.jt.server.service.HazelcastService;
import com.jt.server.service.ScheduledService;

/**
 * Scheduled Echo Task.
 * 
 * @author Hendrik Stein
 * 
 */
public class ScheduledEchoTask extends ScheduledTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Our logger. */
    private static final Logger log = LoggerFactory.getLogger(ScheduledEchoTask.class);

    /** The message. */
    private String message;

    /** Task duration in seconds. */
    private int durationInSeconds;

    /**
     * Create an instance
     * 
     * @param name the scheduled task name
     * @param trigger the trigger
     * @param message the message to echo
     * @param durationInSeconds the duration in seconds
     */
    public ScheduledEchoTask(String name, Trigger trigger, String message, int durationInSeconds) {
        super(name, trigger);
        this.message = message;
        this.durationInSeconds = durationInSeconds;
    }

    @Override
    public void run() {
        // Is this thread able to request the lock
        if (ScheduledService.isRequestLockAllowed(this)) {
            ILock lock = HazelcastService.getTaskLock(getName());
            if (lock.tryLock()) {
                try {
                    Thread.sleep(durationInSeconds * 1000);
                    log.info("Echo " + message + " from " + getName());
                } catch (InterruptedException e) {
                    log.error("Thread interrupted.", e);
                } finally {
                    HazelcastService.setExecutionDateForTask(getName(), DateTime.now());
                    lock.unlock();
                }
            } else {
                // Ignore because other thread holds the lock for this scheduled task
            }
        }
    }
}
