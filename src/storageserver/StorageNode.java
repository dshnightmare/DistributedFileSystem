package storageserver;

import javax.security.auth.login.Configuration;

import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThreadMonitor;

public class StorageNode implements TaskEventListener, CallListener {
	private final static int maxTask = 20;
	private final Storage storage;
	private int taskCount;
	private int taskIDCount;

	StorageNode(Configuration conf, String StorageLocation) {
		storage = new Storage();
		taskCount = 0;
		taskIDCount = 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StorageNode node = new StorageNode(null, "");
		TaskThreadMonitor monitor = TaskThreadMonitor.getInstance();
		monitor.addListener(node);
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub
		// call.getType();
		StorageTaskThread task = new StorageTaskThread(taskIDCount++);
		task.init(call, storage);
		// add listener
		task.run();
	}

	@Override
	public void handle(TaskEvent event) {
		// TODO Auto-generated method stub

	}
}
