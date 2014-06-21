package common.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import common.observe.event.TaskEvent;
import common.observe.event.TaskEvent.Type;
import common.observe.event.TaskEventDispatcher;
import common.observe.event.TaskEventListener;

public class TaskThreadMonitor
    implements TaskEventDispatcher
{
    private TaskThreadPool pool;

    private List<TaskEventListener> listeners = new ArrayList<TaskEventListener>();
    
    private long period;

    private Timer timer = new Timer();

    private TimerTask task = new MonitorTask();

    private boolean isMonitoring = false;

    public TaskThreadMonitor(TaskThreadPool pool, long period)
    {
        this.pool = pool;
        this.period = period;
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
                    fireEvent(new TaskEvent(Type.TASK_FINISHED, thread));
                }
                else if (thread.isLeaseValid())
                {
                    thread.deceaseLease();
                }
                else
                {
                    fireEvent(new TaskEvent(Type.TASK_ABORTED, thread));
                }
            }
        }
    }
}
