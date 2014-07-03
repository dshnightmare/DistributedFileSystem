package storageserver.task;

import common.network.ClientConnector;
import common.network.Connector;
import common.task.Task;

public abstract class StorageServerTask extends Task {
	protected Connector connector;

	protected boolean dead = false;

	public StorageServerTask(long tid) {
		super(tid);
		connector = ClientConnector.getInstance();
	}

	protected void setDead() {
		this.dead = true;
	}

	protected boolean isDead() {
		return dead;
	}

}
