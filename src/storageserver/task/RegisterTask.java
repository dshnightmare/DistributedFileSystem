package storageserver.task;

import java.util.concurrent.TimeUnit;

import storageserver.event.BeforeRegFinishEvent;
import common.call.Call;
import common.call.Call.Type;
import common.call.s2n.RegistrationCallS2N;
import common.util.Configuration;
import common.util.Logger;

public class RegisterTask extends StorageServerTask {
	private final static Logger logger = Logger.getLogger(RegisterTask.class);
	private String address;
	private Boolean finished = false;

	public RegisterTask(long tid, String address) {
		super(tid);
		this.address = address;
	}

	@Override
	public void handleCall(Call call) {
		if (call.getType() == Type.FINISH) {
			synchronized (finished) {
				if (false == finished) {
					finished = true;
					logger.info("StorageServer" + address
							+ " finish registeration.");
					logger.info("------------------->" + call.getFromTaskId());
					fireEvent(new BeforeRegFinishEvent(this,
							call.getFromTaskId()));
				}
			}
		}
	}

	@Override
	public void run() {
		boolean isNSanswer = false;
		while (isNSanswer == false) {
			synchronized (finished) {
				if (false == finished) {
					RegistrationCallS2N call = new RegistrationCallS2N(address);
					call.setFromTaskId(getTaskId());
					connector.sendCall(call);
					logger.info("StorageServer" + address
							+ " send a registerationCall.");
				} else
					isNSanswer = true;
			}
			try {
				TimeUnit.SECONDS.sleep(Configuration.getInstance().getInteger(
						Configuration.SS_REG_INTERVAL));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
