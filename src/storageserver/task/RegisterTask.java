package storageserver.task;

import common.call.Call;
import common.call.s2n.RegistrationCallS2N;
import common.util.Logger;

public class RegisterTask extends StorageServerTask {
	private final static Logger logger = Logger.getLogger(RegisterTask.class);

	public RegisterTask(long tid) {
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
		RegistrationCallS2N call = new RegistrationCallS2N();
		connector.sendCall(call);

	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

}
