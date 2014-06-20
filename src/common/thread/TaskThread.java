package common.thread;

public abstract class TaskThread
    implements Runnable
{
    private long sid;

    private Lease lease = new TaskLease();

    private boolean isFinish = false;

    public TaskThread(long sid)
    {
        this.sid = sid;
    }

    public long getSid()
    {
        return sid;
    }

    // Called by thread itself
    public void renewLease()
    {
        lease.renew();
    }

    // Called by thread monitor
    public void deceaseLease()
    {
        lease.decrease();
    }

    // Called by thread monitor
    public boolean isLeaseValid()
    {
        return lease.isValid();
    }

    public synchronized boolean isFinished()
    {
        return isFinish;
    }

    public synchronized void setFinish()
    {
        isFinish = true;
    }
}
