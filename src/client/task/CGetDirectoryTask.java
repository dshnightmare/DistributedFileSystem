package client.task;

import java.util.List;

import common.call.Call;
import common.call.c2n.GetDirectoryCallC2N;
import common.call.n2c.GetDirectoryCallN2C;
import common.network.ClientConnector;
import common.task.Task;
import common.util.Log;

public class CGetDirectoryTask 
	extends Task{
	
	private String direct;
	private List<String> ret;
	private Object taskWaitor;
	private Object netWaitor = new Object();
	private long toTaskId;
	private GetDirectoryCallN2C callN2C;

	public CGetDirectoryTask(long tid, String direct, List<String> ret, Object waitor) {
		super(tid);
		this.direct = direct;
		this.ret = ret;
		this.taskWaitor = waitor;
	}

	@Override
	public void handleCall(Call call) {
		if (getTaskId() != call.getToTaskId()) {
			return;
		}
		if (call.getType() == Call.Type.GET_DIRECTORY_N2C) {
			callN2C = (GetDirectoryCallN2C)call;
			this.toTaskId = call.getFromTaskId();
			synchronized (netWaitor) {
				netWaitor.notify();
			}
		}
		else if (call.getType() == Call.Type.ABORT) {
			synchronized (taskWaitor) {
				taskWaitor.notify();
			}
		}
		else {
            Log.error("Fatal error: call type dismatch.");
		}
	}

	@Override
	public void run() {
		Log.debug("Query directory: "+direct);
		GetDirectoryCallC2N callC2N = new GetDirectoryCallC2N(direct);
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
		
		//ret = callN2C.getDirectoryList();
		// make sure the same ret location
		List<String> retu = callN2C.getFilesAndDirectories();
		retu.size();
		Log.debug("Query directory returned:");
		for(String item : callN2C.getFilesAndDirectories()){
			ret.add(item);
			Log.debug(" - "+item);
		}
		synchronized (taskWaitor) {
			taskWaitor.notify();
		}
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

}
