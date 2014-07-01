package client.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import common.network.XConnector;
import common.observe.call.AddFileCallN2C;
import common.observe.call.Call;
import common.thread.TaskThread;
import common.util.Log;

public class CAddFileTask 
	extends TaskThread{
	
	/*
	 * get connection with storage server
	 */
	private XConnector xConnector;
	private Socket storageSocket;
	private DataOutputStream out;
	
	private AddFileCallN2C call;

	public CAddFileTask(long tid, Call call) {
		super(tid);
		xConnector = XConnector.getInstance();
		this.call = (AddFileCallN2C)call;
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(call.getLocations().size() == 0){
			Log.print("Fatal error! No storage server returned");
			return;
		}
		
		String location = call.getLocations().get(0);
		storageSocket = xConnector.getSocket(location);
		
		try {
			out = new DataOutputStream(storageSocket.getOutputStream());
			out.writeByte(XConnector.Type.OP_WRITE_BLOCK);
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
