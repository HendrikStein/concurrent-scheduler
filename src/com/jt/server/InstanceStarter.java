package com.jt.server;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jt.server.service.DistributedTaskExecutorService;
import com.jt.server.service.HazelcastService;
import com.jt.server.service.ScheduledService;
import com.jt.server.task.EchoTask;
import com.jt.server.task.ScheduledEchoTask;
import com.jt.server.task.Trigger;
import com.jt.server.task.TriggerTimeUnit;

/**
 * 
 * @author Hendrik Stein
 * 
 */
public class InstanceStarter {
    /** Our logger. */
    private static final Logger log = LoggerFactory.getLogger(InstanceStarter.class);

    /**
     * Start a server instance.
     * 
     * @param args the main arguments
     */
    public static void main(String[] args) {
        // FIXME hstein Add more configuration options via JVM Parameter
        if (args == null || args.length != 2) {
            log.info("usage: InstanceStarter instanceName amounttasks");
            return;
        }

        try {
            int amountTasks = Integer.parseInt(args[1]);
            String nodeName = args[0];

            // Startup the hazelcast node
            HazelcastService.startup();
            InstanceStarter starter = new InstanceStarter();
            starter.attachShutdownHook();
            // starter.runOneTimeTasks(amountTasks, nodeName);
            starter.runScheduledTasks(nodeName);
        } catch (NumberFormatException nfe) {
            log.info("usage: InstanceStarter instanceName amounttasks");
        } catch (InterruptedException e) {
            log.error("Thread interrupted while sleeping.", e);
            return;
        }
    }

    /**
     * Run one time tasks with hazelcast distributed executor.
     * 
     * @param amountTasks the amount of tasks
     * @param nodeName the node name
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    public void runOneTimeTasks(int amountTasks, String nodeName) throws InterruptedException {
        for (int i = 1; i <= amountTasks; i++) {

            // Random duration between 5 and 20 seconds
            int durationInSeconds = getRandom(5, 20);

            // Submitting the new Task
            EchoTask task = new EchoTask("Task No " + i, durationInSeconds);
            log.info("Submitting Task no. " + i + " from " + nodeName);
            DistributedTaskExecutorService.submitTask(task);
        }
    }

    /**
     * Run scheduled tasks
     * 
     * @param nodeName the node name
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    public void runScheduledTasks(String nodeName) throws InterruptedException {
        // log.info("Starting scheduled tasks on node " + nodeName);
        for (int i = 1; i <= 5; i++) {
            // Random duration between 5 and 20 seconds
            int durationInSeconds = getRandom(5, 20);
            Trigger trigger = new Trigger(TriggerTimeUnit.MINUTES, 1);
            ScheduledEchoTask scheduledTask = new ScheduledEchoTask("Task_No_" + i, trigger, "Message " + i + " from "
                    + nodeName, durationInSeconds);
            log.info("Submitting ScheduledTask no. " + i + " from " + nodeName);
            ScheduledService.scheduleTask(scheduledTask);
        }
    }

    /**
     * Attach a shutdown hook for gracefully shutdown
     */
    public void attachShutdownHook() {
        log.info("Attach a shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Graceful shutdown. Stopping all tasks");
                ScheduledService.shutdownNow();
                DistributedTaskExecutorService.shutdownNow();
                HazelcastService.shutdown();
                log.info("All tasks stopped.");
            }
        });
    }

    /**
     * Get a random number.
     * 
     * @param min the minimum
     * @param max the maximum
     * @return the random number
     */
    private int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
