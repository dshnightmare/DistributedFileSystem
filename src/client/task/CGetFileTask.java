package client.task;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import common.network.ClientConnector;
import common.network.XConnector;
import common.call.Call;
import common.call.all.FinishCall;
import common.call.c2n.AddFileCallC2N;
import common.call.c2n.GetFileCallC2N;
import common.call.n2c.AddFileCallN2C;
import common.call.n2c.GetFileCallN2C;
import common.task.Task;
import common.util.IdGenerator;
import common.util.Log;

public class CGetFileTask 
	extends Task{
	
	/*
	 * get connection with storage server
	 */
	private XConnector xConnector;
	private Socket storageSocket;
	private DataOutputStream out;
	
	//wait for the ns to return the call
	private GetFileCallN2C call;
	
	private Object waitor = new Object();
	
	private String filepath;
	private String filename;
	
	private long remoteTaskId;

	public CGetFileTask(long tid, String _path, String _name) {
		super(tid);
		xConnector = XConnector.getInstance();
		filepath = _path;
		filename = _name;
	}

	@Override
	public void handleCall(Call call) {
		if(call.getToTaskId() != getTaskId()){
			return;
		}
		if (call.getType() == Call.Type.ADD_FILE_N2C) {
			this.call = (GetFileCallN2C) call;
			this.remoteTaskId = call.getFromTaskId();
			synchronized (waitor)
            {
				waitor.notify();
            }
		}
		else {
			Log.print("Fatal error: call type dismatch.");
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		GetFileCallC2N callC2N = new GetFileCallC2N(filepath, filename);
		callC2N.setFromTaskId(getTaskId());
		ClientConnector.getInstance().sendCall(callC2N);
		ClientConnector.getInstance().addListener(this);
		
		try {
			synchronized (waitor)
            {
				waitor.wait();
            }
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(call.getLocations().size() == 0){
			Log.print("Fatal error! No storage server returned");
			Log.debug(""+call.getFromTaskId()+" "+call.getToTaskId());
			FinishCall finishCall = new FinishCall();
			call.setToTaskId(remoteTaskId);
			call.setFromTaskId(getTaskId());
			ClientConnector.getInstance().sendCall(finishCall);
			return;
		}
		
		String location = call.getLocations().get(0);
		storageSocket = xConnector.getSocket(location);
		
		try {
			out = new DataOutputStream(storageSocket.getOutputStream());
			out.writeByte(XConnector.Type.OP_WRITE_BLOCK);
			// TODO get file -> id
			out.writeUTF(call.getFileId());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setFinish();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

}
