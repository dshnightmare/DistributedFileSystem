package client.task;

import java.util.concurrent.TimeUnit;

import common.call.c2n.LeaseCallC2N;
import common.network.ClientConnector;
import common.util.Configuration;

/**
 * Extends Thread
 * send lease Call of toTaskId to name server periodically
 * @author gengyufeng
 *
 */
public class CLeaseTask 
	extends Thread{
	/**
	 * NS task id
	 */
	private long toTaskId;
	/**
	 * client task id(not used)
	 */
	private long fromTaskId;
	
	/**
	 * every task start a LeaseTask to send lease
	 * @param fromTaskId	not cared
	 * @param toTaskId	id of the task on NS
	 */
	public CLeaseTask(long fromTaskId, long toTaskId){
		this.toTaskId = toTaskId;
		this.fromTaskId = fromTaskId;
	}
	
	@Override
	public void run(){
		LeaseCallC2N callC2N = new LeaseCallC2N(fromTaskId, toTaskId);
		while (!this.isInterrupted()) {
	        ClientConnector.getInstance().sendCall(callC2N);
	        try {
				TimeUnit.SECONDS.sleep(Configuration.getInstance().getLong(Configuration.LEASE_PERIOD_KEY));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				break;
				//e.printStackTrace();
			}
		}
	}
}
