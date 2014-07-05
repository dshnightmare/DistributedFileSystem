package storageserver.event;

import common.event.TaskEvent;
import common.task.Task;

public class BeforeRegFinishEvent extends TaskEvent {
	private long NStid;

	public BeforeRegFinishEvent(Task thread, long NStid) {
		super(Type.REG_FINISHED, thread);
		// TODO Auto-generated constructor stub
	}

	public long getNStid() {
		return NStid;
	}
}
