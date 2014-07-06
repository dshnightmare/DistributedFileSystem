package client.task;

import common.call.c2n.LeaseCallC2N;
import common.network.ClientConnector;

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
        ClientConnector.getInstance().sendCall(callC2N);
	}
}
