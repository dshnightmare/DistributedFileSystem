package common.thread;

public abstract class TaskThread
    implements Runnable
{
    private long sid;

    private Lease lease = null;

    private boolean isFinish = false;

    private boolean hasLease = false;

    public TaskThread(long sid)
    {
        this.sid = sid;
    }

    public long getSid()
    {
        return sid;
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

    public synchronized boolean isFinished()
    {
        return isFinish;
    }

    public synchronized void setFinish()
    {
        isFinish = true;
    }
    
    @Override
    public abstract void run();
}
