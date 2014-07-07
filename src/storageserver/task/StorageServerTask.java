package storageserver.task;

import common.network.ClientConnector;
import common.task.Task;

/**
 * 
 * @author dengshihong
 * 
 */
public abstract class StorageServerTask extends Task {
	protected ClientConnector connector = ClientConnector.getInstance();

	public StorageServerTask(long tid) {
		super(tid);
	}
}
