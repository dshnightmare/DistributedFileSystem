package common.thread;

import java.util.ArrayList;
import java.util.List;

import common.observe.event.TaskEvent;
import common.observe.event.TaskEventDispatcher;
import common.observe.event.TaskEventListener;

public abstract class TaskThread
    implements Runnable, TaskEventDispatcher
{
    private long tid;

    private Lease lease = null;

    private boolean hasLease = false;

    private List<TaskEventListener> listeners =
        new ArrayList<TaskEventListener>();

    public TaskThread(long tid)
    {
        this.tid = tid;
    }

    public long getTaskId()
    {
        return tid;
    }

    public void setLease(Lease lease)
    {
        this.lease = lease;
        hasLease = true;
    }

    // Called by thread itself
    public void renewLease()
    {
        if (hasLease)
            lease.renew();
    }

    // Called by thread monitor
    public boolean isLeaseValid()
    {
        if (hasLease)
            return lease.isValid();
        else
            return true;
    }

    public void setFinish()
    {
        fireEvent(new TaskEvent(TaskEvent.Type.TASK_FINISHED, this));
    }

    @Override
    public final void addListener(TaskEventListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public final void removeListener(TaskEventListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public final void fireEvent(TaskEvent event)
    {
        for (TaskEventListener l : listeners)
            l.handle(event);
    }

    @Override
    public abstract void run();

    /**
     * Release resources such as locks, in order to GC
     */
    public abstract void release();
}
