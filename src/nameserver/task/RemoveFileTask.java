package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Meta;
import common.network.Connector;
import common.call.Call;
import common.call.c2n.RemoveFileCallC2N;
import common.util.Logger;

public class RemoveFileTask extends NameServerTask {
	private final static Logger logger = Logger.getLogger(RemoveFileTask.class);

	private String dirName;

	private String fileName;

	public RemoveFileTask(long tid, Call call, Connector connector) {
		super(tid, call, connector);
		RemoveFileCallC2N c = (RemoveFileCallC2N) call;
		this.dirName = c.getDirName();
		this.fileName = c.getFileName();
	}

	@Override
	public void run() {
		final BackupUtil backup = BackupUtil.getInstance();

		synchronized (Meta.getInstance()) {
			if (!fileExists()) {
				sendAbortCall("Task aborted, file does not exist.");
				setFinish();
			} else {
				logger.info("RemoveFileTask " + getTaskId() + " started.");
				backup.writeLogIssue(getTaskId(), Call.Type.REMOVE_FILE_C2N,
						dirName + " " + fileName);

				logger.info("RemoveFileTask " + getTaskId() + " commit.");
				backup.writeLogCommit(getTaskId());

				Meta.getInstance().removeFile(dirName, fileName);
				setFinish();
			}
		}
	}

	@Override
	public void release() {
		setDead();
	}

	@Override
	public void handleCall(Call call) {
		if (call.getToTaskId() != getTaskId())
			return;

		if (call.getType() == Call.Type.LEASE_C2N) {
			renewLease();
			return;
		}
	}

	private boolean fileExists() {
		return Meta.getInstance().containFile(dirName, fileName);
	}
}
