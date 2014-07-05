package storageserver.task;

import common.call.Call;
import common.call.Call.Type;
import common.call.n2s.MigrateFileCallN2S;
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
		if (call.getType() == Type.MIGRATE_FILE_N2S) {
			synchronized (finished) {
				finished = true;
			}
			logger.info("StorageServer recieve registration answer");
		}
	}

	@Override
	public void run() {
		// TODO 需要处理异常情况：如果一直没有收到回复需要重新
		boolean isNSanswer = false;
		while (isNSanswer == false) {
			synchronized (finished) {
				if (false == finished) {
					RegistrationCallS2N call = new RegistrationCallS2N();
					connector.sendCall(call);
				} else
					isNSanswer = true;
			}
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
