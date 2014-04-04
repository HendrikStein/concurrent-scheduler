package com.jt.server.task;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;

/**
 * Example Echo Task.
 * 
 * @author Hendrik Stein
 * 
 */
public class EchoTask implements Callable<String>, Serializable, HazelcastInstanceAware {
    private static final long serialVersionUID = 3213199604434142364L;

    /** The message to repeat. */
    private String message;

    /** Hazelcast node instance where this echo task is called. */
    private HazelcastInstance hazelcast;

    /** Pretend task duration. */
    private int taskDurationInSeconds;

    /**
     * Create an instance.
     * 
     * @param message the message
     * @param taskDuration the task duration in seconds
     */
    public EchoTask(String message, int taskDurationInSeconds) {
        this.message = message;
        this.taskDurationInSeconds = taskDurationInSeconds;
    }

    @Override
    public String call() throws Exception {
        long durationInMillis = taskDurationInSeconds * 1000;
        Thread.sleep(durationInMillis);
        Cluster cluster = hazelcast.getCluster();
        return "Echo " + message + " from " + cluster.getLocalMember() + ". Echo Task duration "
                + taskDurationInSeconds + " seconds.";
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcast = hazelcastInstance;
    }
}
