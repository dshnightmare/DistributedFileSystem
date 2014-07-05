package storageserver.task;


import common.call.Call;
import common.call.s2n.HeartbeatCallS2N;

public class HeartbeatTask extends StorageServerTask {

	private static final Object syn = new Object();
	public HeartbeatTask(long tid) {
		super(tid);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		while(true)
		{
			// TODO 在heartbit
			HeartbeatCallS2N call = new HeartbeatCallS2N(null);
			connector.sendCall(call);
			try {
				Thread.sleep(1000);
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
