package common.task;

import java.util.ArrayList;
import java.util.List;

import common.call.CallListener;
import common.event.TaskEvent;
import common.event.TaskEventDispatcher;
import common.event.TaskEventListener;

/**
 * Abstract Task.
 * <p>
 * Each task represents one process. Such as getting file, renaming directory or
 * sending heartbeat, etc.
 * <p>
 * It implements <tt>Runnable</tt>, <tt>TaskEventDispatcher</tt> and
 * <tt>CallListener</tt>.
 * 
 * @author lishunyang
 * @see Runnable
 * @see TaskEventDispatcher
 * @see CallListener
 */
public abstract class Task
    implements Runnable, TaskEventDispatcher, CallListener
{
    /**
     * Task id.
     */
    private long tid;

    /**
     * Task lease.
     * <p>
     * It is optional.
     */
    private Lease lease = null;

    /**
     * Indicate whether the task has lease.
     */
    private boolean hasLease = false;

    /**
     * List of <tt>TaskEventListener</tt>
     */
    private List<TaskEventListener> listeners =
        new ArrayList<TaskEventListener>();

    /**
     * Construction method.
     * 
     * @param tid
     */
    public Task(long tid)
    {
        this.tid = tid;
    }

    /**
     * Get task id.
     * 
     * @return
     */
    public long getTaskId()
    {
        return tid;
    }

    /**
     * Set lease.
     * 
     * @param lease
     */
    public void setLease(Lease lease)
    {
        this.lease = lease;
        hasLease = true;
    }

    /**
     * Renew task lease.
     */
    public void renewLease()
    {
        if (hasLease)
            lease.renew();
    }

    /**
     * Test whether task lease is valid.
     * 
     * @return
     */
    public boolean isLeaseValid()
    {
        if (hasLease)
            return lease.isValid();
        else
            return true;
    }

    // TODO: Actually "finish" is not very suitable. When a task is aborted,
    // we also think it's finished. For instance, client send a rename call to
    // name server, but the original file didn't exist, so name server send
    // abort call back to client and let that task finish. So we should change
    // the opion of this method and related stuff.
    /**
     * Notify event listens this task has finished.
     */
    public void setFinish()
    {
        fireEvent(new TaskEvent(TaskEvent.Type.TASK_FINISHED, this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addListener(TaskEventListener listener)
    {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeListener(TaskEventListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void fireEvent(TaskEvent event)
    {
        for (TaskEventListener l : listeners)
            l.handle(event);
    }

    /**
     * Task thread method.
     */
    @Override
    public abstract void run();

    /**
     * Release the resources hold by this task.
     * <p>
     * <strong>Warning:</strong> This method should only be called when task is
     * aborted.
     */
    public abstract void release();
}
