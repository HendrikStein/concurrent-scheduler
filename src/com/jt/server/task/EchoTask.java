package com.jt.server.task;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.jt.server.util.RandomUtils;

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

    /**
     * Create an instance.
     * 
     * @param message the message
     */
    public EchoTask(String message) {
        this.message = message;
    }

    @Override
    public String call() throws Exception {
        int durationInSeconds = RandomUtils.getRandomNumber(5, 20);
        Thread.sleep(durationInSeconds * 1000);
        Cluster cluster = hazelcast.getCluster();
        return "Echo " + message + " from " + cluster.getLocalMember() + ". Echo Task duration "
                + durationInSeconds + " seconds.";
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcast = hazelcastInstance;
    }
}
