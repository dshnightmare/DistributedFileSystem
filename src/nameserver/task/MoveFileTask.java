package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Meta;
import common.network.Connector;
import common.call.Call;
import common.call.c2n.MoveFileCallC2N;
import common.util.Logger;

public class MoveFileTask extends NameServerTask {
	private final static Logger logger = Logger.getLogger(MoveFileTask.class);

	private String oldDirName;

	private String oldFileName;

	private String newDirName;

	private String newFileName;

	public MoveFileTask(long tid, Call call, Connector connector) {
		super(tid, call, connector);
		MoveFileCallC2N c = (MoveFileCallC2N) call;
		this.oldDirName = c.getOldDirName();
		this.oldFileName = c.getOldFileName();
		this.newDirName = c.getNewDirName();
		this.newFileName = c.getNewFileName();
	}

	@Override
	public void run() {
		final BackupUtil backup = BackupUtil.getInstance();

		synchronized (Meta.getInstance()) {
			if (!oldFileExists()) {
				sendAbortCall("Task aborted, old file does not exist.");
				setFinish();
			} else if (newFileExists()) {
				sendAbortCall("Task aborted, new file has arealdy existed.");
				setFinish();
			} else {
				logger.info("MoveFileTask " + getTaskId() + " started.");
				backup.writeLogIssue(getTaskId(), Call.Type.MOVE_FILE_C2N,
						oldDirName + " " + oldFileName + " " + newDirName + " "
								+ newFileName);

				logger.info("MoveFileTask " + getTaskId() + " commit.");
				backup.writeLogCommit(getTaskId());

				Meta.getInstance().renameFile(oldDirName, oldFileName,
						newDirName, newFileName);
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

	private boolean oldFileExists() {
		if (Meta.getInstance().containFile(oldDirName, oldFileName))
			return true;
		else
			return false;
	}

	private boolean newFileExists() {
		if (Meta.getInstance().containFile(newDirName, newFileName))
			return true;
		else
			return false;
	}
}
