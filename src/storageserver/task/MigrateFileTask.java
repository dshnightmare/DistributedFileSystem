package storageserver.task;

import java.util.List;

import common.call.Call;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

}
