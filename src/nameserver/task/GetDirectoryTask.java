package nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nameserver.BackupUtil;
import nameserver.meta.Directory;
import nameserver.meta.Meta;
import nameserver.meta.Storage;
import common.call.AbortCall;
import common.call.Call;
import common.call.GetDirectoryCallC2N;
import common.call.GetDirectoryCallN2C;
import common.call.GetFileCallC2N;
import common.call.GetFileCallN2C;
import common.network.Connector;
import common.task.Task;
import common.util.Logger;

public class GetDirectoryTask extends Task {
	private final static Logger logger = Logger
			.getLogger(GetDirectoryTask.class);

	private String dirName;

	private Connector connector;

	private String initiator;

	private long remoteTaskId;

	private List<String> fileList;

	private List<String> dirList;

	public GetDirectoryTask(long tid, Call call, Connector connector) {
		super(tid);
		GetDirectoryCallC2N c = (GetDirectoryCallC2N) call;
		this.dirName = c.getDirName();
		this.connector = connector;
		this.initiator = c.getInitiator();
		this.remoteTaskId = call.getFromTaskId();
	}

	@Override
	public void handleCall(Call call) {
		if (call.getToTaskId() != getTaskId())
			return;

		if (call.getType() == Call.Type.LEASE) {
			renewLease();
			return;
		}

	}

	@Override
	public void run() {
		final Meta meta = Meta.getInstance();

		synchronized (meta) {

			if (!directoryExists()) {
				sendAbortCall("Task aborted, directory does not exist.");
				return;
			} else {
				logger.info("GetDirectoryTask " + getTaskId() + " started.");

				Directory dir = meta.getDirectory(dirName);

				fileList = new ArrayList<String>();
				dirList = new ArrayList<String>();

				for (String fname : dir.getValidFileNameList().keySet())
					fileList.add(fname);

				for (String dname: meta.getSubDirectoryName(dirName))
					dirList.add(dname);

				logger.info("GetDirectoryTask " + getTaskId() + " commit.");

				sendResponseCall();
				setFinish();
			}
		}
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	private boolean directoryExists() {
		if (Meta.getInstance().containDirectory(dirName))
			return true;
		else
			return false;
	}

	private void sendAbortCall(String reason) {
		Call back = new AbortCall(reason);
		back.setFromTaskId(getTaskId());
		back.setToTaskId(remoteTaskId);
		back.setInitiator(initiator);
		connector.sendCall(back);
		release();
		setFinish();
	}

	private void sendResponseCall() {
		Call back = new GetDirectoryCallN2C(fileList, dirList);
		back.setFromTaskId(getTaskId());
		back.setToTaskId(remoteTaskId);
		back.setInitiator(initiator);
		connector.sendCall(back);
	}
}
