package common.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import common.event.TaskEvent;
import common.event.TaskEvent.Type;
import common.event.TaskEventDispatcher;
import common.event.TaskEventListener;
import common.util.Configuration;
import common.util.Logger;

// FIXME: Code refactoring, replace timer with scheduled task executor.
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
     * Task event listeners, if some task's lease is invalid, notify them.
     */
    private List<TaskEventListener> listeners =
        new ArrayList<TaskEventListener>();

    /**
     * Checking period. (second)
     */
    private long period;

    // FIXME: Relpace it with scheduled task executor.
    /**
     * Timer.
     */
    private Timer timer = new Timer();

    /**
     * Monitoring thread, it will wake up regularly and check all running tasks.
     */
    private TimerTask monitor = new Monitor();

    /**
     * Indicate whether the monitor is working.
     */
    private boolean isMonitoring = false;

    /**
     * Construction method.
     */
    public TaskMonitor()
    {
        this.period =
            Configuration.getInstance().getLong(Configuration.LEASE_PERIOD_KEY) * 2;
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
     * Turn on <tt>TaskMonitor</tt> switch.
     */
    public void startMonitoring()
    {
        if (!isMonitoring)
        {
            timer.scheduleAtFixedRate(monitor, 0, period);
            isMonitoring = true;
        }
    }

    /**
     * Turn off <tt>TaskMonitor</tt> switch.
     */
    public void stopMonitoring()
    {
        if (isMonitoring)
        {
            timer.cancel();
            isMonitoring = false;
        }
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
        synchronized (tasks)
        {
            tasks.remove(event.getTaskThread());
            logger.debug("TASK_FINISHED");
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
                    fireEvent(new TaskEvent(Type.TASK_ABORTED,
                        tasks.get(taskId)));
                    listeners.remove(tasks.remove(taskId));
                    logger.info("Task " + taskId
                        + " is aborted because of invalid lease.");
                }
            }
        }
    }
}
