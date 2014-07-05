package storageserver.task;

import java.util.concurrent.TimeUnit;

import storageserver.Storage;
import common.call.Call;
import common.call.all.AbortCall;
import common.call.n2s.SyncCallN2S;
import common.call.s2n.SyncCallS2N;
import common.util.Configuration;
import common.util.Logger;

public class SyncTask extends StorageServerTask {
	private final static Logger logger = Logger.getLogger(SyncTask.class);
	private Boolean alive = true;
	private String address;
	private Storage storage;

	public SyncTask(long tid, Storage storage, String address) {
		super(tid);
		this.storage = storage;
		this.address = address;
	}

	@Override
	public void handleCall(Call call) {
		if (call.getType() == Call.Type.SYNC_N2S) {
			SyncCallN2S mycall = (SyncCallN2S)call;
			storage.removefiles(mycall.getFiles());
		} else if (call.getType() == Call.Type.ABORT) {
			logger.info("Reason: " + ((AbortCall)call).getReason());
			alive = false;
		}
	}

	@Override
	public void run() {
		while (alive) {
			SyncCallS2N call = new SyncCallS2N(address, storage.analyzeCurrentFiles());
			call.setFromTaskId(getTaskId());
			connector.sendCall(call);
			try {
				TimeUnit.SECONDS.sleep(Configuration.getInstance().getInteger(
						Configuration.SS_SYNC_INTERVAL));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setFinish();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

}
