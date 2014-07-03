package nameserver.task;

import common.call.Call;
import common.task.Task;

public class NameServerTask {
	private Task task;
	
	public NameServerTask(Call call)
	{
		switch (call.getType())
		{
		case ABORT:
			break;
		case ADD_DIRECTORY_C2N:
			break;
		case ADD_FILE_C2N:
			break;
		case ADD_FILE_N2C:
			break;
		case ADD_FILE_SS:
			break;
		case APPEND_FILE_C2N:
			break;
		case APPEND_FILE_N2C:
			break;
		case FINISH:
			break;
		case GET_DIRECTORY_C2N:
			break;
		case GET_DIRECTORY_N2C:
			break;
		case GET_FILE_C2N:
			break;
		case GET_FILE_N2C:
			break;
		case GET_FILE_SS:
			break;
		case HEARTBEAT_S2N:
			break;
		case INVALID:
			break;
		case LEASE:
			break;
		case MIGRATE_FILE_N2S:
			break;
		case MOVE_FILE_C2N:
			break;
		case REGISTRATION_S2N:
			break;
		case REMOVE_FILE_C2N:
			break;
		case SYNC_N2S:
			break;
		case SYNC_S2N:
			break;
		default:
			break;
		}
	}
}
