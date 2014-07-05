package storageserver.task;

import common.network.ClientConnector;
import common.network.Connector;
import common.task.Task;

public abstract class StorageServerTask extends Task {
	protected Connector connector;
	protected Boolean finished = false;
	protected Boolean dead = false;

	public StorageServerTask(long tid) {
		super(tid);
		connector = ClientConnector.getInstance();
	}

	protected void setDead() {
		this.dead = true;
	}

	protected Boolean isDead() {
		return dead;
	}
	
	protected void setFinished() {
		this.finished = true;
	}

	protected Boolean isFinished() {
		return finished;
	}

}
