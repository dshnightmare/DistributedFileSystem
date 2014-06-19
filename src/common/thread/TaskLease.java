package common.thread;

public class TaskLease implements Lease {

	private long lease = 0;

	@Override
	public synchronized void renew() {
		lease = 5;
	}

	@Override
	public synchronized boolean isValid() {
		return lease > 0;
	}

	@Override
	public synchronized void decrease() {
		lease -= 1;
	}

}
