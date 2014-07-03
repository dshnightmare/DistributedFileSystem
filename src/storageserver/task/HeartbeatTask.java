package storageserver.task;

import java.util.concurrent.Semaphore;

import common.call.Call;
import common.call.s2n.RegistrationCallS2N;
import common.task.Task;

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
		// TODO Auto-generated method stub
		synchronized (syn) {
			try {
				RegistrationCallS2N call = new RegistrationCallS2N("");
				connector.sendCall(call);
				syn.wait();
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
