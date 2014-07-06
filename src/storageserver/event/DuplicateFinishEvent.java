package storageserver.event;

import common.event.TaskEvent;
import common.task.Task;

/**
 * 
 * @author dengshihong
 * 
 */
public class DuplicateFinishEvent extends TaskEvent {
	private final long parent;
	private final byte status;

	public DuplicateFinishEvent(Task thread, long parent, byte status) {
		super(Type.DUPLICATE_FINISHED, thread);
		this.parent = parent;
		this.status = status;
	}

	public long getParent() {
		return parent;
	}

	public byte getStatus() {
		return status;
	}

}
