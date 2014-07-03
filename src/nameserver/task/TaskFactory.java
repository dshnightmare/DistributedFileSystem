package nameserver.task;

import common.call.Call;

public class TaskFactory {
	public static NameServerTask createTask(Call call) {
		NameServerTask task = new NameServerTask(call);

		return task;
	}
}
