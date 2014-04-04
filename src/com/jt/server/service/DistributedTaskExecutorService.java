package com.jt.server.service;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IExecutorService;

/**
 * Hazelcast distributed executor service. This should be used for one time jobs. For circular jobs use the
 * {@link ScheduledService}
 * 
 * @author Hendrik Stein
 * 
 */
public final class DistributedTaskExecutorService {

    /** Our logger. */
    private static final Logger log = LoggerFactory.getLogger(DistributedTaskExecutorService.class);

    private DistributedTaskExecutorService() {
        // Static access only. Utility class
    }

    /**
     * Shutdown distributed executor by now and ignore the list of waiting tasks.
     */
    public static void shutdownNow() {
        HazelcastService.getExecutor().shutdownNow();
    }

    /**
     * Submit a {@link Callable} task to run it with the hazelcast {@link IExecutorService}.
     * 
     * @param callableTask the task to execute
     */
    public static void submitTask(Callable<String> callableTask) {
        IExecutorService executor = HazelcastService.getExecutor();

        executor.submit(callableTask, new ExecutionCallback<String>() {
            public void onResponse(String response) {
                log.info(response);
            }

            public void onFailure(Throwable t) {
                log.error("Error while executing task", t);
            }
        });
    }
}
