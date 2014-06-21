package common.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import common.observe.event.TaskEvent;
import common.observe.event.TaskEvent.EventType;
import common.observe.event.TaskEventDispatcher;
import common.observe.event.TaskEventListener;

public class TaskThreadMonitor
    implements TaskEventDispatcher
{
    private TaskThreadPool pool;

    private List<TaskEventListener> listeners = new ArrayList<TaskEventListener>();

    private Timer timer = new Timer();

    private TimerTask task = new MonitorTask();

    private boolean isMonitoring = false;

    public TaskThreadMonitor(TaskThreadPool pool)
    {
        pool = this.pool;
    }

    public void startMonitoring(long period)
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

    public void restartMonitoring(long period)
    {
        stopMonitoring();
        startMonitoring(period);
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
            l.handleEvent(event);
    }

    private class MonitorTask
        extends TimerTask
    {

        @Override
        public void run()
        {
            TaskThread thread = pool.nextThread();
            if (null != thread)
            {
                if (thread.isFinished())
                {
                    fireEvent(new TaskEvent(EventType.TASK_FINISHED, thread));
                }
                else if (thread.isLeaseValid())
                {
                    thread.deceaseLease();
                }
                else
                {
                    fireEvent(new TaskEvent(EventType.TASK_ABORTED, thread));
                }
            }
        }
    }
}
