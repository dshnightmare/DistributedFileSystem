package common.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import common.event.TaskEvent;
import common.event.TaskEvent.Type;
import common.event.TaskEventDispatcher;
import common.event.TaskEventListener;
import common.util.Configuration;
import common.util.Logger;

/**
 * <tt>TaskMonitor</tt> is used for checking tasks status, whether they are
 * aborted or finished. If something happens, it will notify
 * <tt>TaskEventListener</tt>.
 * 
 * @author lishunyang
 * @see TaskEventListner
 * @see TaskEventDispatcher
 */
public class TaskMonitor
    implements TaskEventDispatcher, TaskEventListener
{
    /**
     * Logger.
     */
    private static Logger logger = Logger.getLogger(TaskMonitor.class);

    /**
     * Tasks that would be checking.
     */
    private Map<Long, Task> tasks = new HashMap<Long, Task>();

    /**
     * Monitor thread executor.
     */
    private ScheduledExecutorService monitor = Executors
        .newSingleThreadScheduledExecutor();

    /**
     * Task event listeners, if some task's lease is invalid, notify them.
     */
    private List<TaskEventListener> listeners =
        new ArrayList<TaskEventListener>();

    /**
     * Checking period. (second)
     */
    private long period;

    /**
     * Construction method.
     */
    public TaskMonitor()
    {
        this.period =
            Configuration.getInstance().getLong(Configuration.LEASE_PERIOD_KEY) * 2;
        monitor.scheduleAtFixedRate(new Monitor(), 0, period, TimeUnit.SECONDS);

        logger.info("Task monitor started. Checking period: " + period + "s.");
    }

    /**
     * Add task into monitoring list and start monitoring.
     * <p>
     * Monitoring process won't start if <tt>TaskMonitor</tt> is stopped.
     * 
     * @param thread
     */
    public synchronized void addTask(Task thread)
    {
        tasks.put(thread.getTaskId(), thread);
        thread.addListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(TaskEventListener listener)
    {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeListener(TaskEventListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fireEvent(TaskEvent event)
    {
        for (TaskEventListener l : listeners)
            l.handle(event);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Once a task has finished, it will notify the <tt>TaskMonitor</tt> and the
     * monitor will forward this note to registered <tt>TaskEventListener</tt>s.
     */
    @Override
    public void handle(TaskEvent event)
    {
        fireEvent(event);

        if (TaskEvent.Type.TASK_FINISHED == event.getType()
            || TaskEvent.Type.TASK_DUE == event.getType())
        {
            synchronized (tasks)
            {
                Task task = tasks.remove(event.getTaskThread().getTaskId());
                logger.info("Task: " + task.getClass() + " has finished, "
                    + event.getType() + " removed from TaskMonitor.");
            }
        }
    }

    /**
     * Inner class of Monitoring task. It will wake up regularly and check all
     * tasks.
     * 
     * @author lishunyang
     * 
     */
    private class Monitor
        extends TimerTask
    {
        /**
         * Check the lease validation. If someone has timeout, fire an abort
         * event.
         */
        @Override
        public void run()
        {
            synchronized (tasks)
            {
                List<Long> abortedList = new ArrayList<Long>();
                for (Entry<Long, Task> e : tasks.entrySet())
                {
                    if (!e.getValue().isLeaseValid())
                        abortedList.add(e.getKey());
                }
                for (Long taskId : abortedList)
                {
                    fireEvent(new TaskEvent(Type.TASK_DUE, tasks.get(taskId)));
                    listeners.remove(tasks.remove(taskId));
                    logger.info("Task " + taskId
                        + " is aborted because of invalid lease.");
                }
            }
        }
    }
}
