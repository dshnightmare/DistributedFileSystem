package common.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import common.observe.event.TaskEvent;
import common.observe.event.TaskEvent.Type;
import common.observe.event.TaskEventDispatcher;
import common.observe.event.TaskEventListener;
import common.util.Logger;

public class TaskThreadMonitor
    implements TaskEventDispatcher, TaskEventListener
{
    private Queue<TaskThread> threads = new LinkedList<TaskThread>();

    private List<TaskEventListener> listeners =
        new ArrayList<TaskEventListener>();

    private long period;

    private Timer timer = new Timer();

    private TimerTask task = new MonitorTask();

    private boolean isMonitoring = false;

    private static Logger logger = Logger.getLogger(TaskThreadMonitor.class);

    public TaskThreadMonitor(long period)
    {
        this.period = period;
    }

    public synchronized void addThread(TaskThread thread)
    {
        threads.add(thread);
    }

    public void startMonitoring()
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

    public void restartMonitoring()
    {
        stopMonitoring();
        startMonitoring();
    }

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
                Iterator<TaskThread> iter = threads.iterator();
                while (iter.hasNext())
                {
                    TaskThread thread = iter.next();
                    if (!thread.isLeaseValid())
                    {
                        fireEvent(new TaskEvent(Type.TASK_ABORTED, thread));
                        iter.remove();
                        logger.debug("TASK_ABORTED");
                    }
                }
            }
        }
    }
}
