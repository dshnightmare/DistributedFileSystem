package nameserver.task;

import common.call.Call;

public class TaskFactory {
	public static NameServerTaskThread createTask(Call call) {
		NameServerTaskThread task = new NameServerTaskThread(call);

		return task;
	}
}
