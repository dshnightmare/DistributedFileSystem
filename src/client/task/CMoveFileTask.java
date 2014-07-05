package client.task;

import java.util.List;

import common.call.Call;
import common.call.c2n.AddDirectoryCallC2N;
import common.call.c2n.MoveFileCallC2N;
import common.call.n2c.GetDirectoryCallN2C;
import common.network.ClientConnector;
import common.task.Task;

public class CMoveFileTask 	
	extends Task{
	
	private String direct;
	private Object netWaitor = new Object();
	private String oldDir, oldName;
	private String newDir, newName;
	
	
	public CMoveFileTask(long tid, String oldDir, String oldName
			, String newDir, String newName) {
		super(tid);
		this.oldDir = oldDir;
		this.oldName = oldName;
		this.newDir = newDir;
		this.newName = newName;
	}


	@Override
	public void handleCall(Call call) {
		if (getTaskId() != call.getToTaskId()) {
			return;
		}
		if(call.getType() == Call.Type.FINISH
				||call.getType() == Call.Type.ABORT){
			synchronized (netWaitor) {
				netWaitor.notify();
			}
		}
		
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		MoveFileCallC2N callC2N = new MoveFileCallC2N(oldDir, oldName, newDir, newName);
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
		setFinish();
	}


	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}
}
