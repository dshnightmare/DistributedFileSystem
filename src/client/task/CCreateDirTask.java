package client.task;

import java.util.List;

import common.call.Call;
import common.call.c2n.AddDirectoryCallC2N;
import common.call.n2c.GetDirectoryCallN2C;
import common.network.ClientConnector;
import common.task.Task;

public class CCreateDirTask 	extends Task{
	
	private String direct;
	private Object taskWaitor;
	private Object netWaitor = new Object();
	private long toTaskId;
	private GetDirectoryCallN2C callN2C;
	
	
	public CCreateDirTask(long tid, String direct, Object waitor) {
		super(tid);
		this.direct = direct;
		this.taskWaitor = waitor;
	}


	@Override
	public void handleCall(Call call) {
		if (getTaskId() != call.getToTaskId()) {
			return;
		}
		k
		
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		AddDirectoryCallC2N callC2N = new AddDirectoryCallC2N(direct);
		callC2N.setFromTaskId(getTaskId());
		ClientConnector.getInstance().sendCall(callC2N);
		ClientConnector.getInstance().addListener(this);
		synchronized (netWaitor) {
			try {
				netWaitor.wait();
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
