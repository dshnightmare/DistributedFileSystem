package test.nameserver.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.HeartbeatTask;
import common.network.ServerConnector;
import common.network.XConnector;
import common.call.Call;
import common.call.CallListener;
import common.call.FinishCall;
import common.call.HeartbeatCallS2N;
import common.call.MigrateFileCallN2S;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.Task;

public class TestHeartbeatTask extends TestCase {
	private static ServerConnector NConnector;

	private static XConnector SConnector;

	@Override
	protected void setUp() {
		NConnector = ServerConnector.getInstance();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		SConnector = XConnector.getInstance();
		NConnector.addListener(new NCallListener());
		SConnector.addListener(new SCallListener());
	}

	public void testTask() {
		Storage storage = new Storage(1, "localhost");
		Status.getInstance().addStorage(storage);
		long timestamp = storage.getHearbeatTime();

		HeartbeatCallS2N call = new HeartbeatCallS2N("localhost",
				new HashMap<String, List<String>>());
		call.setToTaskId(1);
		SConnector.sendCall(call);

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		storage = Status.getInstance().getStorage("localhost");
		assertTrue(storage.getHearbeatTime() > timestamp);
	}

	@Override
	protected void tearDown() {
	}

	private class NCallListener implements CallListener {
		@Override
		public void handleCall(Call call) {
			System.out.println("<---: " + call.getType());
			if (Call.Type.HEARTBEAT_S2N == call.getType()) {
				Task task = new HeartbeatTask(1, call, NConnector, 2000);
				task.addListener(new TaskListener());
				new Thread(task).start();
			}
		}
	}

	private class SCallListener implements CallListener {
		@Override
		public void handleCall(Call call) {
			System.out.println("--->: " + call.getType());
			if (Call.Type.MIGRATE_FILE_N2S == call.getType()) {
				MigrateFileCallN2S c = (MigrateFileCallN2S) call;
				for (Entry<String, List<String>> s : c.getFiles().entrySet()) {
					System.out.println("Get from storage: " + s.getKey());
					for (String id : s.getValue())
						System.out.println("\t" + id);
				}
				Call back = new FinishCall();
				back.setToTaskId(1);
				SConnector.sendCall(back);
			}
		}
	}

	private class TaskListener implements TaskEventListener {
		@Override
		public void handle(TaskEvent event) {
			System.out.println("Task " + event.getTaskThread().getTaskId()
					+ " " + event.getType());
		}
	}
}
