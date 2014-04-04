package com.jt.server.service;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ILock;

/**
 * Provides access to distributed hazelcast objects.
 * 
 * @author Hendrik Stein
 * 
 */
public final class HazelcastService {

    /** Singleton hazelcast instance. */
    private static HazelcastInstance instance;

    private HazelcastService() {
        // Static access only. Utility class
    }

    /**
     * Get the node {@link HazelcastInstance}
     * 
     * The first access to this method will init the hazelcast node. For Production mode this node instance should be
     * initialized during server startup.
     * 
     * @return the hazelcast node instance
     */
    private synchronized static HazelcastInstance getInstance() {
        if (instance == null) {
            /** Read config from hazelcast.xml file. */
            Config config = new Config();
            instance = Hazelcast.newHazelcastInstance(config);
        }
        return instance;
    }

    /**
     * Get a lock for the task name.
     * 
     * @return the task lock
     */
    public static ILock getTaskLock(String taskName) {
        return getInstance().getLock(taskName);
    }

    /**
     * Get the distributed executor service.
     * 
     * @return the distributed executor service
     */
    public static IExecutorService getExecutor() {
        /** The Executor service needs to be configured in the hazelcast.xml */
        return getInstance().getExecutorService("default");
    }

    /**
     * Start the hazelcast node
     */
    public static void startup() {
        getInstance();
    }

    /**
     * Shutdown the Hazelcast node
     */
    public static void shutdown() {
        getInstance().shutdown();
    }

    /**
     * Get last execution date for task.
     * 
     * @param taskName the task name
     * @return the last execution date. If it is never run before return <tt>null</tt>
     */
    public static Date getExecutionDateForTask(String taskName) {
        return getScheduledTaskMap().get(taskName);
    }

    /**
     * Set execution date for task.
     * 
     * @param taskName the task name
     * @param executionDate the execution date
     */
    public static void setExecutionDateForTask(String taskName, DateTime executionDate) {
        getScheduledTaskMap().put(taskName, executionDate.toDate());
    }

    /**
     * Return the scheduled tasks map.
     * 
     * @return the scheduled task map
     */
    private static Map<String, Date> getScheduledTaskMap() {
        /**
         * The Map needs to be configured in the hazelcast.xml. Hazelcast can only handle (java.util.Date) for the
         * in-memory-format 'OBJECT' Serialization. Date transformation is needed.
         */
        return getInstance().getMap("default");
    }
}
