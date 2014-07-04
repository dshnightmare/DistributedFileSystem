package common.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.util.Logger;

/**
 * Executor of task.
 * <p>
 * Encapsulate tasks into thread and execute them.
 * 
 * @author lishunyang
 * 
 */
public class TaskExecutor
{
    /**
     * Logger.
     */
    private final static Logger logger = Logger.getLogger(TaskExecutor.class);

    /**
     * Max count of running task thread.
     */
    private final static int MAX_TASK = 20;

    /**
     * Thread pool.
     */
    private ExecutorService executor = Executors.newFixedThreadPool(MAX_TASK);

    /**
     * Construction method.
     */
    public TaskExecutor()
    {
    }

    /**
     * Execute task.
     * 
     * @param task
     */
    public synchronized void executeTask(Task task)
    {
        logger.info("Fire a task");
        executor.execute(task);
    }

    /**
     * Shutdown executor. Reject coming
     * tasks and wait for running tasks finish and 
     */
    public synchronized void shutDown()
    {
        logger.info("shutdown");
        executor.shutdown();
    }
}
