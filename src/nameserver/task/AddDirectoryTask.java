package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Directory;
import nameserver.meta.Meta;
import common.call.Call;
import common.call.c2n.AddDirectoryCallC2N;
import common.network.Connector;
import common.util.Logger;

public class AddDirectoryTask extends NameServerTask {
	private final static Logger logger = Logger
			.getLogger(AddDirectoryTask.class);

	private String dirName;

	public AddDirectoryTask(long tid, Call call, Connector connector) {
		super(tid, call, connector);
		AddDirectoryCallC2N c = (AddDirectoryCallC2N) call;
		this.dirName = c.getDirName();
	}

	@Override
	public void handleCall(Call call) {
	}

	@Override
	public void run() {
		final Meta meta = Meta.getInstance();
		final BackupUtil backup = BackupUtil.getInstance();

		synchronized (meta) {
			if (directoryExists()) {
				sendAbortCall("Task aborted, there has been a directory with the same name.");
				return;
			} else {
				Directory dir = new Directory(dirName);

				logger.info("AddDirectoryTask " + getTaskId() + " started.");
				backup.writeLogIssue(getTaskId(), Call.Type.ADD_FILE_C2N,
						dirName + " " + dirName);

				logger.info("AddDirectoryTask " + getTaskId() + " commit.");
				backup.writeLogCommit(getTaskId());

				meta.addDirectory(dir);
			}

			setFinish();
		}
	}

	@Override
	public void release() {
	}

	private boolean directoryExists() {
		if (Meta.getInstance().containDirectory(dirName))
			return true;
		else
			return false;
	}
}
