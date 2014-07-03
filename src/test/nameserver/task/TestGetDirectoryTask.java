package test.nameserver.task;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.GetDirectoryTask;
import common.call.Call;
import common.call.CallListener;
import common.call.c2n.GetDirectoryCallC2N;
import common.call.n2c.GetDirectoryCallN2C;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.task.Task;

public class TestGetDirectoryTask extends TestCase {
	private static Task task;

	private static ServerConnector NConnector;

	private static ClientConnector CConnector;

	@Override
	protected void setUp() {
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
		final Storage storage = new Storage("localhost");
		final Meta meta = Meta.getInstance();
		File file = null;
		
		Status.getInstance().addStorage(storage);
		file = new File("f1", 1);
		file.setValid(true);
		file.addLocation(storage);
		meta.addFile("/a/b/c/", file);
		file = new File("f2", 2);
		file.setValid(true);
		file.addLocation(storage);
		meta.addFile("/a/b/d", file);
		file = new File("f3", 3);
		file.setValid(true);
		file.addLocation(storage);
		meta.addFile("/a/", file);

		assertNotNull(meta.getDirectory("/a/"));
		assertNotNull(meta.getDirectory("/a/b/"));

		GetDirectoryCallC2N call = new GetDirectoryCallC2N("/a/");
		CConnector.sendCall(call);

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void tearDown() {
	}

	private class NCallListener implements CallListener {
		@Override
		public void handleCall(Call call) {
			System.out.println("<---: " + call.getType());
			if (Call.Type.GET_DIRECTORY_C2N == call.getType()) {
				task = new GetDirectoryTask(1, call, NConnector);
				task.addListener(new TaskListener());
				new Thread(task).start();
			} else if (Call.Type.FINISH_C2N == call.getType()) {
				task.handleCall(call);
			}
		}
	}

	private class CCallListener implements CallListener {

		@Override
		public void handleCall(Call call) {
			System.out.println("--->: " + call.getType());
			if (Call.Type.GET_DIRECTORY_N2C == call.getType()) {
				GetDirectoryCallN2C c = (GetDirectoryCallN2C) call;
				System.out.println("call type: " + c.getType());
				System.out.print("files: ");
				for (String l : c.getFileList())
					System.out.print(l + " ");
				System.out.println();
				System.out.println("directories:");
				for (String l : c.getDirectoryList())
					System.out.print(l + " ");
				System.out.println();
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
