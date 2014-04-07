package com.jt.server;

import org.apache.commons.lang3.StringUtils;
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
 * Represents an instance.
 * 
 * @author Hendrik Stein
 * 
 */
public class InstanceStarter {
    /** Our logger. */
    private static final Logger log = LoggerFactory.getLogger(InstanceStarter.class);

    /** */
    public static String NODE_NAME = "";

    /**
     * Start a server instance.
     * 
     * @param args the main arguments
     */
    public static void main(String[] args) {
        // Eva
        String scheduleAmountTasks = System.getProperty("schedule.amouttasks");
        String scheduleTimeunit = System.getProperty("schedule.timeunit");
        String scheduleInterval = System.getProperty("schedule.period");
        String onetimeAmounttasks = System.getProperty("onetime.amounttasks");
        NODE_NAME = System.getProperty("node.name");
        if (StringUtils.isEmpty(scheduleAmountTasks) || StringUtils.isEmpty(scheduleTimeunit)
                || StringUtils.isEmpty(scheduleInterval) || StringUtils.isEmpty(onetimeAmounttasks)
                || StringUtils.isEmpty(NODE_NAME)) {
            log.info("usage: InstanceStarter -Dnode.name=NODE_1 -Dschedule.amouttasks={value} -Dschedule.timeunit=MINUTE -Dschedule.period={value} -Donetime.amounttasks={value}");
            return;
        }

        try {
            int amountOneTimeTasks = Integer.parseInt(onetimeAmounttasks);
            int amountScheduledTasks = Integer.parseInt(scheduleAmountTasks);
            TriggerTimeUnit triggerTimeUnit = TriggerTimeUnit.getInstance(scheduleTimeunit);
            int triggerInterval = Integer.parseInt(scheduleInterval);

            // Startup the hazelcast node
            HazelcastService.startup();
            InstanceStarter starter = new InstanceStarter();
            starter.attachShutdownHook();
            starter.runOneTimeTasks(amountOneTimeTasks);
            starter.runScheduledTasks(amountScheduledTasks, triggerTimeUnit, triggerInterval);
        } catch (IllegalArgumentException e) {
            log.info("usage: InstanceStarter -Dnode.name=NODE_1 -Dschedule.amouttasks={value} -Dschedule.timeunit=MINUTE -Dschedule.period={value} -Donetime.amounttasks={value}");
        } catch (InterruptedException e) {
            log.error("Thread interrupted while sleeping.", e);
            return;
        }
    }

    /**
     * Run one time tasks with hazelcast distributed executor.
     * 
     * @param amountTasks the amount of tasks
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    public void runOneTimeTasks(int amountTasks) throws InterruptedException {
        for (int i = 1; i <= amountTasks; i++) {

            // Submitting the new Task
            EchoTask task = new EchoTask("Task No " + i);
            log.info("Submitting Task no. " + i + " from " + NODE_NAME);
            DistributedTaskExecutorService.submitTask(task);
        }
    }

    /**
     * Run scheduled tasks
     * 
     * @param nodeName the node name
     * @param amountTasks the amount of scheduled tasks
     * @param triggerTimeUnit the triggerTimeUnit
     * @param triggerInterval the triggerInterval
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    public void runScheduledTasks(int amountTasks, TriggerTimeUnit triggerTimeUnit, int triggerInterval)
            throws InterruptedException {
        for (int i = 1; i <= amountTasks; i++) {
            Trigger trigger = new Trigger(triggerTimeUnit, triggerInterval);
            ScheduledEchoTask scheduledTask = new ScheduledEchoTask("Task_No_" + i, trigger, "Message " + i);
            log.info("Submitting ScheduledTask no. " + i + " from " + NODE_NAME);
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
}
