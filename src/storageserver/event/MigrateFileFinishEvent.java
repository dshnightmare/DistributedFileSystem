package storageserver.event;

import java.util.List;

import common.event.TaskEvent;
import common.task.Task;

/**
 * 
 * @author dengshihong
 * 
 */
public class MigrateFileFinishEvent extends TaskEvent {
	private final String address;
	private final List<String> files;

	public MigrateFileFinishEvent(Task thread, String address,
			List<String> files) {
		super(TaskEvent.Type.MIGRATE_FINISHED, thread);
		this.address = address;
		this.files = files;
	}

	public String getAddress() {
		return address;
	}

	public List<String> getFiles() {
		return files;
	}

}
