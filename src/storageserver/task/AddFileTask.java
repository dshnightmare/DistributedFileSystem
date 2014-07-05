package storageserver.task;

import java.net.Socket;

import common.call.Call;
import common.task.Task;

public class AddFileTask extends StorageServerTask {
	Socket socket;

	public AddFileTask(long tid, Socket socket) {
		super(tid);
		this.socket = socket;
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

}
