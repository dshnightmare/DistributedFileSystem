package storageserver.event;

import common.event.TaskEvent;
import common.task.Task;

/**
 * 
 * @author dengshihong
 * 
 */
public class BeforeRegFinishEvent extends TaskEvent {
	private long NStid;

	public BeforeRegFinishEvent(Task thread, long NStid) {
		super(Type.REG_FINISHED, thread);
		this.NStid = NStid;
	}

	public long getNStid() {
		return NStid;
	}
}
