package storageserver.event;

import java.util.List;
import java.util.Map;

import common.event.TaskEvent;
import common.task.Task;

public class HeartbeatResponseEvent extends TaskEvent {
	private Map<String, List<String>> working;
	public HeartbeatResponseEvent(Task thread, Map<String, List<String>> working) {
		super(Type.HEARTBEAT_RESPONSE, thread);
		this.working = working;
	}
	public Map<String, List<String>> getWorking() {
		return working;
	}
}
