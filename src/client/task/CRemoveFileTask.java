package client.task;

import common.call.Call;
import common.call.c2n.RemoveFileCallC2N;
import common.network.ClientConnector;
import common.task.Task;
import common.util.Log;

public class CRemoveFileTask 
	extends Task{
	private String dir, name;
	private Object netWaitor = new Object();
	private long toTaskId;

	public CRemoveFileTask(long tid, String dir, String name) {
		super(tid);
		this.dir = dir;
		this.name = name;
		// TODO Auto-generated constructor stub
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
		Log.debug("DeleteFile:"+dir+name);
		RemoveFileCallC2N callC2N = new RemoveFileCallC2N(dir, name);
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
