package common.protocol;

/**
 * report for StorageNode
 * @author dsh
 */
public class StorageReport {
	private final long capacity;
	private final long dfsused;
	private final long remaining;
	
	public StorageReport(long capacity, long dfsused, long remaining)
	{
		this.capacity = capacity;
		this.dfsused = dfsused;
		this.remaining = remaining;
	}

	public long getCapacity() {
		return capacity;
	}

	public long getDfsused() {
		return dfsused;
	}

	public long getRemaining() {
		return remaining;
	}

}
