package nameserver.task;

import common.call.Call;
import common.task.Task;
import common.util.IdGenerator;

public class NameServerTaskThread {
	private Task task;

	public NameServerTaskThread(Call call) {
		long taskId = IdGenerator.getInstance().getLongId();

		// Name server only care about call: *_C2N.
		switch (call.getType()) {
		case ADD_DIRECTORY_C2N:
			break;
		case ADD_FILE_C2N:
			break;
		case APPEND_FILE_C2N:
			break;
		case FINISH_C2N:
			break;
		case GET_DIRECTORY_C2N:
			break;
		case GET_FILE_C2N:
			break;
		case LEASE_C2N:
			break;
		case MOVE_FILE_C2N:
			break;
		case REMOVE_FILE_C2N:
			break;
		default:
			break;
		}
	}
}
