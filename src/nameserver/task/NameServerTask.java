package nameserver.task;

import common.call.Call;
import common.call.n2c.AbortCallN2C;
import common.network.Connector;
import common.task.Task;

public abstract class NameServerTask extends Task {

	private Connector connector;

	private String initiator;

	private long remoteTaskId;

	public NameServerTask(long tid, Call call, Connector connector) {
		super(tid);
		this.connector = connector;
		this.initiator = call.getInitiator();
		this.remoteTaskId = call.getFromTaskId();
	}

	/**
	 * Send abort call.
	 * 
	 * @param reason
	 */
	protected void sendAbortCall(String reason) {
		sendCall(new AbortCallN2C(reason));
	}

	protected void sendCall(Call call) {
		call.setFromTaskId(getTaskId());
		call.setToTaskId(remoteTaskId);
		call.setInitiator(initiator);

		connector.sendCall(call);
	}
}
