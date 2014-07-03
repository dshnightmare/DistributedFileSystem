package nameserver.task;

import java.util.ArrayList;
import java.util.List;

import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import common.network.Connector;
import common.call.Call;
import common.call.n2s.SyncCallN2S;
import common.call.s2n.SyncCallS2N;
import common.util.Logger;

public class SyncTask extends NameServerTask {
	private final static Logger logger = Logger.getLogger(SyncTask.class);

	private String address;

	private List<Long> files;

	private int duplicate;

	public SyncTask(long tid, Call call, Connector connector, int duplicate) {
		super(tid, call, connector);
		SyncCallS2N c = (SyncCallS2N) call;
		this.address = c.getAddress();
		this.files = c.getFiles();
		this.duplicate = duplicate;
	}

	@Override
	public void run() {
		logger.info("SyncTask started.");

		synchronized (Meta.getInstance()) {
			if (!storageExists()) {
				sendAbortCall("Task aborted, unidentified storage server.");
				setFinish();
			} else {
				List<Long> removeList = new ArrayList<Long>();
				for (Long l : files) {
					File file = Meta.getInstance().getFile(l);
					if (null == file)
						removeList.add(l);
					else {
						if (file.getLocationsCount() > duplicate)
							removeList.add(l);
						else
							file.addLocation(Status.getInstance().getStorage(
									address));
					}
				}
				sendResponseCall(removeList);
				setFinish();
			}
		}
	}

	@Override
	public void release() {
	}

	@Override
	public void handleCall(Call call) {
	}

	private boolean storageExists() {
		return Status.getInstance().contains(address);
	}

	private void sendResponseCall(List<Long> removeList) {
		Call back = new SyncCallN2S(removeList);
		sendCall(back);
	}
}
