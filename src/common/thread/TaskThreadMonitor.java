package common.thread;

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

public class TaskThreadMonitor
    implements TaskEventDispatcher, TaskEventListener
{
    private static TaskThreadMonitor instance = null;

    private Map<Long, TaskThread> threads = new HashMap<Long, TaskThread>();

    private List<TaskEventListener> listeners =
        new ArrayList<TaskEventListener>();

    private long period;

    private Timer timer = new Timer();

    private TimerTask task = new MonitorTask();

    private boolean isMonitoring = false;

    private static Logger logger = Logger.getLogger(TaskThreadMonitor.class);

    private TaskThreadMonitor()
    {
        this.period =
            Configuration.getInstance().getLong(Configuration.LEASE_PERIOD_KEY) * 2;
    }

    public static TaskThreadMonitor getInstance()
    {
        if (null == instance)
        {
            synchronized (TaskThreadMonitor.class)
            {
                if (null == instance)
                {
                    instance = new TaskThreadMonitor();
                    instance.startMonitoring();
                }
            }
        }

        return instance;
    }

    public synchronized void addThread(TaskThread thread)
    {
        threads.put(thread.getTaskId(), thread);
        thread.addListener(this);
    }

    private void startMonitoring()
    {
        if (!isMonitoring)
        {
            timer.scheduleAtFixedRate(task, 0, period);
            isMonitoring = true;
        }
    }

    public void stopMonitoring()
    {
        if (isMonitoring)
        {
            timer.cancel();
            isMonitoring = false;
        }
    }

//    private void restartMonitoring()
//    {
//        stopMonitoring();
//        startMonitoring();
//    }

    @Override
    public synchronized void addListener(TaskEventListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public synchronized void removeListener(TaskEventListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void fireEvent(TaskEvent event)
    {
        for (TaskEventListener l : listeners)
            l.handle(event);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Once a task has finished, it will notify task monitor and taskmonitor
     * will notify its listeners.
     */
    @Override
    public void handle(TaskEvent event)
    {
        fireEvent(event);
        synchronized (threads)
        {
            threads.remove(event.getTaskThread());
            logger.debug("TASK_FINISHED");
        }
    }

    private class MonitorTask
        extends TimerTask
    {
        @Override
        public void run()
        {
            synchronized (threads)
            {
                List<Long> abortedList = new ArrayList<Long>();
                for (Entry<Long, TaskThread> e : threads.entrySet())
                {
                    if (!e.getValue().isLeaseValid())
                        abortedList.add(e.getKey());
                }
                for (Long l : abortedList)
                {
                    fireEvent(new TaskEvent(Type.TASK_ABORTED, threads.get(l)));
                    listeners.remove(threads.remove(l));
                    logger.debug("TASK_ABORTED");
                }
            }
        }
    }
}
