package storageserver.task;

import java.net.Socket;
import java.util.List;

import common.call.Call;
import common.network.XConnector;
import common.task.Task;

public class MigrateFileTask extends StorageServerTask {
	private String address;
	private List<String> filenames;

	public MigrateFileTask(long tid, String address, List<String> filenames) {
		super(tid);
		this.address = address;
		this.filenames = filenames;
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		String[] string = address.split(":");
		for (String filename : filenames) {
			//Socket socket = XConnector.getSocket(string[0], Integer.parseInt(string[1]));
		}
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

}
