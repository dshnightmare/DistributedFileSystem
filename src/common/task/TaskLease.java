package common.task;

public class TaskLease implements Lease {

	private long timestamp = 0;
	private long period = 0;
	
	public TaskLease(long period)
	{
	    timestamp = System.currentTimeMillis();
	    this.period = period;
	}

	@Override
	public synchronized void renew() {
		timestamp = System.currentTimeMillis();
	}

	@Override
	public synchronized boolean isValid() {
	    long current = System.currentTimeMillis();
	    return (current - timestamp) < period;
	}
}
