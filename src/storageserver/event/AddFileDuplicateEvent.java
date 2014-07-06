package storageserver.event;

import java.util.List;

import common.event.TaskEvent;
import common.task.Task;

public class AddFileDuplicateEvent extends TaskEvent {
	private final String filename;
	private final List<String> todo;

	public AddFileDuplicateEvent(Task thread, String filename, List<String> todo) {
		super(Type.ADDFILE_DUPLICATE, thread);
		this.filename = filename;
		this.todo = todo;
	}

	public String getFilename() {
		return filename;
	}

	public List<String> getTodo() {
		return todo;
	}

}
