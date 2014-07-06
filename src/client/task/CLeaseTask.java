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

	private long toTaskId;
	private long fromTaskId;
	
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
