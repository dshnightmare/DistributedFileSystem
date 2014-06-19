package common.thread;

public abstract class TaskThread implements Runnable {
	private long sid;
	private Lease lease = new TaskLease();

	public TaskThread(long sid) {
		this.sid = sid;
	}

	public long getSid() {
		return sid;
	}
	
	// Called by thread itself
	public void renewLease() {
		lease.renew();
	}
	
	// Called by thread monitor
	public void deceaseLease() {
		lease.decrease();
	}
	
	// Called by thread monitor
	public boolean isLeaseValid() {
		return lease.isValid();
	}
}
