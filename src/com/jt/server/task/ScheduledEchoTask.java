package com.jt.server.task;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ILock;
import com.jt.server.InstanceStarter;
import com.jt.server.service.HazelcastService;
import com.jt.server.service.ScheduledService;
import com.jt.server.util.RandomUtils;

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

    /**
     * Create an instance
     * 
     * @param name the scheduled task name
     * @param trigger the trigger
     * @param message the message to echo
     */
    public ScheduledEchoTask(String name, Trigger trigger, String message) {
        super(name, trigger);
        this.message = message;
    }

    @Override
    public void run() {
        // Is this thread able to request the lock
        if (ScheduledService.isRequestLockAllowed(this)) {
            ILock lock = HazelcastService.getTaskLock(getName());
            printLockInfos(lock);
            if (lock.tryLock()) {
                log.info("Lock from " + InstanceStarter.NODE_NAME + " for " + getName() + " acquired at "
                        + DateTime.now());
                try {
                    int durationInSeconds = RandomUtils.getRandomNumber(5, 20);
                    Thread.sleep(durationInSeconds * 1000);
                    log.info("Echo " + message + " from " + InstanceStarter.NODE_NAME + " for " + getName()
                            + " Duration: " + durationInSeconds + " secs");
                } catch (InterruptedException e) {
                    log.error("Thread interrupted.", e);
                } finally {
                    HazelcastService.setExecutionDateForTask(getName(), DateTime.now());
                    lock.unlock();
                    log.info("Unlock from " + InstanceStarter.NODE_NAME + " for " + getName() + " at "
                            + DateTime.now() + "\n");
                }
            } else {
                // Ignore because other thread holds the lock for this scheduled task
                log.info("Could not acquire the lock from " + InstanceStarter.NODE_NAME + " for " + getName() + "\n");
            }
        } else {
            log.info("This task has already been triggered by another scheduler task. The trigger interval is not elapsed yet. "
                    + getTrigger().toString());
        }
    }

    /**
     * Print lock infos
     * 
     * @param lock the hazelcast lock
     */
    private void printLockInfos(ILock lock) {
        log.info(lock.getName() + ", " + lock.getPartitionKey() + ", " + lock.getServiceName());
        log.info("Is locked: " + lock.isLocked());
        log.info("Lock count: " + lock.getLockCount());
    }
}
