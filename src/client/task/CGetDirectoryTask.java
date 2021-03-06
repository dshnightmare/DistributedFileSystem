package client.task;

import java.util.List;

import common.call.Call;
import common.call.all.AbortCall;
import common.call.c2n.GetDirectoryCallC2N;
import common.call.n2c.GetDirectoryCallN2C;
import common.network.ClientConnector;
import common.task.Task;
import common.util.Log;

/**
 * task to get contents of target directory
 * @author gengyufeng
 *
 */
public class CGetDirectoryTask 
	extends Task{
	/**
	 * target directory
	 */
	private String direct;
	/**
	 * contents of the target directory, globally used
	 */
	private List<String> ret;
	/**
	 * getDirectory operation is Sync, the method blocks on this object
	 */
	private Object taskWaitor;
	/**
	 * wait on this object for NS to response
	 */
	private Object netWaitor = new Object();
	private long toTaskId;
	private GetDirectoryCallN2C callN2C;

	/**
	 * This task will BLOCK untill finished!
	 * @param tid	globally unique task id
	 * @param direct	target directory
	 * @param ret	contents returned
	 * @param waitor	blocking object
	 */
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
				Log.info(((AbortCall)call).getReason());
				taskWaitor.notify();
			}
		}
		else {
            Log.error("Fatal error: call type dismatch.");
		}
	}

	@Override
	public void run() {
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
		for(String item : callN2C.getFilesAndDirectories()){
			Log.debug(" - "+item);
			ret.add(item);
		}
		synchronized (taskWaitor) {
			taskWaitor.notify();
		}
		setFinish();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

}
