package client.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
	
	private Socket storageSocket;
	private DataOutputStream out;
	private DataInputStream dis;
	private FileOutputStream fos;
	private File file;
	
	//wait for the ns to return the call
	private GetFileCallN2C call;
	
	private Object waitor = new Object();
	
	private String filepath;
	private String filename;
	
	private long toTaskId;

	public CGetFileTask(long tid, String _path, String _name, File file) {
		super(tid);
		filepath = _path;
		filename = _name;
		this.file = file;
	}

	@Override
	public void handleCall(Call call) {
		if(call.getToTaskId() != getTaskId()){
			return;
		}
		if (call.getType() == Call.Type.ADD_FILE_N2C) {
			this.call = (GetFileCallN2C) call;
			this.toTaskId = call.getFromTaskId();
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
        
    	CLeaseTask leaseTask = new CLeaseTask(getTaskId(), toTaskId);
    	leaseTask.start();
        
		if(call.getLocations().size() == 0){
			Log.error("Fatal error! No storage server returned");
			setFinish();
			return;
		}

        String location = call.getLocations().get(0);
        String[] locationStrings = location.split(":");
        storageSocket = XConnector.getSocket(locationStrings[0], Integer.parseInt(locationStrings[1]));
        byte status = XConnector.Type.OP_FINISH_FAIL;
		
		try {
			out = new DataOutputStream(storageSocket.getOutputStream());
			dis = new DataInputStream(storageSocket.getInputStream());
			fos = new FileOutputStream(file);
			out.writeByte(XConnector.Type.OP_READ_BLOCK);

			long length = dis.readLong();
			byte[] inputByte = new byte[1024];
			int toreadlen = (length < inputByte.length) ? (int)length : inputByte.length;
			int readlen;
			while(length > 0 && (readlen = dis.read(inputByte, 0, toreadlen)) > 0){
				fos.write(inputByte, 0, readlen);
				fos.flush();
				length -=  readlen;
				toreadlen = (length < inputByte.length) ? (int)length : inputByte.length;
			}
			out.writeByte(XConnector.Type.OP_FINISH_SUC);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				status = XConnector.Type.OP_FINISH_SUC;
				out.close();
				dis.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        if (status == XConnector.Type.OP_FINISH_FAIL) {
			Log.error("CGetFileTask Download failed");
			return;
		}
		FinishCall finishCall = new FinishCall();
		finishCall.setToTaskId(toTaskId);
		finishCall.setFromTaskId(getTaskId());
		ClientConnector.getInstance().sendCall(finishCall);
        setFinish();
        leaseTask.interrupt();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

}
