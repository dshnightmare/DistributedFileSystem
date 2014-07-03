package test.nameserver.task;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.Directory;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.AddDirectoryTask;
import common.call.Call;
import common.call.CallListener;
import common.call.c2n.AddDirectoryCallC2N;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.task.Task;

public class TestAddDirectoryTask extends TestCase {
	private static ServerConnector NConnector;

	private static ClientConnector CConnector;

	private static Task task;

	@Override
	protected void setUp() {
		Status.getInstance().addStorage(new Storage(1, "localhost"));
		NConnector = ServerConnector.getInstance();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CConnector = ClientConnector.getInstance();
		CConnector.addListener(new CCallListener());
		NConnector.addListener(new NCallListener());
	}

	public void testTask() {
		Meta meta = Meta.getInstance();

		Directory dir = meta.getDirectory("/a/");
		assertNull(dir);

		AddDirectoryCallC2N call = new AddDirectoryCallC2N("/a/");
		CConnector.sendCall(call);

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		synchronized (meta) {
			assertNotNull(meta.getDirectory("/a/"));
		}
	}

	@Override
	protected void tearDown() {
	}

	private class NCallListener implements CallListener {
		@Override
		public void handleCall(Call call) {
			System.out.println("<---: " + call.getType());
			if (Call.Type.ADD_DIRECTORY_C2N == call.getType()) {
				task = new AddDirectoryTask(1, call, NConnector);
				task.addListener(new TaskListener());
				new Thread(task).start();
			} else if (Call.Type.FINISH == call.getType()) {
				task.handleCall(call);
			}
		}
	}

	private class CCallListener implements CallListener {
		@Override
		public void handleCall(Call call) {
			System.out.println("--->: " + call.getType());
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
