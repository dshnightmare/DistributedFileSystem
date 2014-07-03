package nameserver.task;

import java.util.ArrayList;
import java.util.List;

import nameserver.meta.Directory;
import nameserver.meta.Meta;
import common.call.Call;
import common.call.c2n.GetDirectoryCallC2N;
import common.call.n2c.GetDirectoryCallN2C;
import common.network.Connector;
import common.util.Logger;

public class GetDirectoryTask extends NameServerTask {
	private final static Logger logger = Logger
			.getLogger(GetDirectoryTask.class);

	private String dirName;

	private List<String> fileList;

	private List<String> dirList;

	public GetDirectoryTask(long tid, Call call, Connector connector) {
		super(tid, call, connector);
		GetDirectoryCallC2N c = (GetDirectoryCallC2N) call;
		this.dirName = c.getDirName();
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

	@Override
	public void run() {
		final Meta meta = Meta.getInstance();

		synchronized (meta) {

			if (!directoryExists()) {
				sendAbortCall("Task aborted, directory does not exist.");
				setFinish();
				return;
			} else {
				logger.info("GetDirectoryTask " + getTaskId() + " started.");

				Directory dir = meta.getDirectory(dirName);

				fileList = new ArrayList<String>();
				dirList = new ArrayList<String>();

				for (String fname : dir.getValidFileNameList().keySet())
					fileList.add(fname);

				for (String dname : meta.getSubDirectoryName(dirName))
					dirList.add(dname);

				logger.info("GetDirectoryTask " + getTaskId() + " commit.");

				sendResponseCall();
				setFinish();
			}
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

	private void sendResponseCall() {
		Call back = new GetDirectoryCallN2C(fileList, dirList);
		sendCall(back);
	}
}
