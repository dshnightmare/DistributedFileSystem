package common.task;

/**
 * An implementation of <tt>Lease</tt>.
 * 
 * @author lishunyang
 * @see Lease
 */
public class TaskLease
    implements Lease
{
    /**
     * The time stamp when this lease has been created.
     */
    private long timestamp = 0;

    /**
     * How long this lease will be valid. (seconds)
     */
    private long period = 0;

    /**
     * Construction method.
     * 
     * @param period
     */
    public TaskLease(long period)
    {
        timestamp = System.currentTimeMillis();
        this.period = period;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void renew()
    {
        timestamp = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean isValid()
    {
        long current = System.currentTimeMillis();
        return (current - timestamp) < period;
    }
}
