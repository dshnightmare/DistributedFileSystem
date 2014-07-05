package storageserver.task;

import common.network.ClientConnector;
import common.task.Task;

public abstract class StorageServerTask extends Task {
	protected ClientConnector connector;

	public StorageServerTask(long tid) {
		super(tid);
	}
}
