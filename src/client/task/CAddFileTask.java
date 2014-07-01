package client.task;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import common.network.ClientConnector;
import common.network.XConnector;
import common.observe.call.AddFileCallC2N;
import common.observe.call.AddFileCallN2C;
import common.observe.call.Call;
import common.thread.TaskThread;
import common.util.IdGenerator;
import common.util.Log;

public class CAddFileTask 
	extends TaskThread{
	
	/*
	 * get connection with storage server
	 */
	private XConnector xConnector;
	private Socket storageSocket;
	private DataOutputStream out;
	
	//wait for the ns to return the call
	private AddFileCallN2C call;
	private long taskId;
	
	private Object waitor = new Object();
	
	private String filepath;
	private String filename;

	public CAddFileTask(long tid, String _path, String _name) {
		super(tid);
		xConnector = XConnector.getInstance();
		filepath = _path;
		filename = _name;
		taskId = IdGenerator.getInstance().getLongId().longValue();
	}

	@Override
	public void handleCall(Call call) {
		if(call.getClientTaskId() != taskId){
			return;
		}
		if (call.getType() == Call.Type.ADD_FILE_N2C) {
			this.call = (AddFileCallN2C) call;
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
		
		AddFileCallC2N callC2N = new AddFileCallC2N(taskId
				, filepath, filename);
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
			return;
		}
		
		String location = call.getLocations().get(0);
		storageSocket = xConnector.getSocket(location);
		
		try {
			out = new DataOutputStream(storageSocket.getOutputStream());
			out.writeByte(XConnector.Type.OP_WRITE_BLOCK);
			// TODO get file -> id
			out.writeLong(call.getFileId());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

}
