package storageserver.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import common.call.Call;
import common.call.n2s.MigrateFileCallN2S;
import common.call.s2n.HeartbeatCallS2N;

public class HeartbeatTask extends StorageServerTask {
	private Map<String, List<String>> overMigrateFile;
	private Map<String, List<String>> onMigrateFile;

	public HeartbeatTask(long tid, Map<String, List<String>> overMigrateFile,
			Map<String, List<String>> onMigrateFile) {
		super(tid);
		this.overMigrateFile = overMigrateFile;
		this.onMigrateFile = onMigrateFile;
	}

	@Override
	public void handleCall(Call call) {
		if (call.getType() == Call.Type.MIGRATE_FILE_N2S) {
			MigrateFileCallN2S mycall = (MigrateFileCallN2S) call;
			Map<String, List<String>> recieve = mycall.getFiles();
			Map<String, List<String>> working = new HashMap<String, List<String>>();
			if (null == recieve || recieve.isEmpty())
				return;
			synchronized (overMigrateFile) {
				synchronized (onMigrateFile) {
					for (String key : recieve.keySet()) {
						working.put(key, new ArrayList<String>());
						for (String filename : recieve.get(key)) {
							if (null != overMigrateFile.get(key)
									&& overMigrateFile.get(key).contains(
											filename)) {
								// 迁移任务已经在完成队列
							} else if (null != onMigrateFile.get(key)
									&& onMigrateFile.get(key)
											.contains(filename)) {
								// 迁移任务已经在进行队列
							} else {
								working.get(key).add(filename);
								if (null == onMigrateFile.get(key))
									onMigrateFile.put(key,
											new ArrayList<String>());
								onMigrateFile.get(key).add(filename);
							}
						}
					}
				}
			}
			fireEvent(new HeartbeatResponseEvent(this, working));
		}

	}

	@Override
	public void run() {
		Map<String, List<String>> migratefile = new HashMap<String, List<String>>();
		while (true) {
			synchronized (overMigrateFile) {
				migratefile.clear();
				migratefile.putAll(overMigrateFile);
				overMigrateFile.clear();
			}
			HeartbeatCallS2N call = new HeartbeatCallS2N(migratefile);
			call.setFromTaskId(getTaskId());
			connector.sendCall(call);
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

}
