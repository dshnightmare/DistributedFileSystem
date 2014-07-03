package nameserver;

import common.call.Call;
import common.call.c2n.FinishCallC2N;
import common.call.n2c.AbortCallN2C;
import common.network.Connector;

public class CallUtil {
	private static CallUtil instance = new CallUtil();

	private CallUtil() {
	}

	public static CallUtil getInstatnce() {
		return instance;
	}

	/**
	 * Send abort call.
	 * 
	 * @param connector
	 * @param fromTaskId
	 * @param toTaskId
	 * @param initiator
	 * @param reason
	 */
	public void sendAbortCall(Connector connector, long fromTaskId,
			long toTaskId, String initiator, String reason) {
		Call call = new AbortCallN2C(reason);

		call.setFromTaskId(fromTaskId);
		call.setToTaskId(toTaskId);
		call.setInitiator(initiator);

		connector.sendCall(call);
	}

	/**
	 * Send finish call.
	 * 
	 * @param connector
	 * @param fromTaskId
	 * @param toTaskId
	 * @param initiator
	 */
	public void sendFinishCall(Connector connector, long fromTaskId,
			long toTaskId, String initiator) {
		Call call = new FinishCallC2N();
		call.setFromTaskId(fromTaskId);
		call.setToTaskId(toTaskId);
		call.setInitiator(initiator);

		connector.sendCall(call);
	}
}
