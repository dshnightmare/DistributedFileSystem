package client.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import common.network.ClientConnector;
import common.network.XConnector;
import common.call.Call;
import common.call.all.AbortCall;
import common.call.all.FinishCall;
import common.call.c2n.AddFileCallC2N;
import common.call.n2c.AddFileCallN2C;
import common.task.Task;
import common.util.IdGenerator;
import common.util.Log;

/**
 * Send ADD_FILE_CALL to NS, wait for response of SS locations
 * setup socket with SS and send data
 * Sent FINISH to NS when transmission finished
 * @author gengyufeng
 *
 */
public class CAddFileTask
    extends Task
{

    /*
     * get connection with storage server
     */
    private XConnector xConnector;

    private Socket storageSocket;

    private DataOutputStream out;
    private DataInputStream in;

    // wait for the ns to return the call
    private AddFileCallN2C call;

    private Object waitor = new Object();

    private String filepath;

    private String filename;
    private File file;
    
    private long toTaskId;
    private Call.Type type;

    public CAddFileTask(long tid, String _path, String _name, File file)
    {
        super(tid);
        filepath = _path;
        filename = _name;
        this.file = file;
    }

    @Override
    public void handleCall(Call call)
    {
        if (call.getToTaskId() != getTaskId())
        {
            return;
        }
        if (call.getType() == Call.Type.ADD_FILE_N2C)
        {
            this.call = (AddFileCallN2C) call;
            this.toTaskId = call.getFromTaskId();
			type = call.getType();
            synchronized (waitor)
            {
                waitor.notify();
            }
        }
        if (call.getType() == Call.Type.ABORT) {
			Log.error(((AbortCall)call).getReason());
			type = call.getType();
		}
        else
        {
            Log.error("Fatal error: call type dismatch.");
        }
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub
    	
        AddFileCallC2N callC2N = new AddFileCallC2N(filepath, filename);
        callC2N.setFromTaskId(getTaskId());
        ClientConnector.getInstance().sendCall(callC2N);
        ClientConnector.getInstance().addListener(this);

        try
        {
            synchronized (waitor)
            {
                waitor.wait();
            }
        }
        catch (InterruptedException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
    	CLeaseTask leaseTask = new CLeaseTask(getTaskId(), toTaskId);
    	leaseTask.start();
        
        if (type == Call.Type.ABORT) {
			return;
		}
        
        if (call.getLocations().size() == 0)
        {
            Log.print("Fatal error! No storage server returned");
            setFinish();
            return;
        }

        String location = call.getLocations().get(0);
        String[] locationStrings = location.split(":");
        storageSocket = XConnector.getSocket(locationStrings[0], Integer.parseInt(locationStrings[1]));
        byte status = XConnector.Type.OP_FINISH_FAIL;
        try
        {
        	long fileLength = file.length();
        	FileInputStream fis = new FileInputStream(file);
        	
        	
            out = new DataOutputStream(storageSocket.getOutputStream());
            in = new DataInputStream(storageSocket.getInputStream());
            out.writeByte(XConnector.Type.OP_WRITE_BLOCK);
            out.writeInt(call.getLocations().size()-1);//other ss to send
            for(int i=1; i<call.getLocations().size(); i++){
            	out.writeUTF(call.getLocations().get(i));
            }
            out.writeUTF(call.getFileId());//file name
            out.writeLong(fileLength);//file size
            
            byte[] sendBytes = new byte[1024];
            int length, sumL=0;
			while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
				sumL += length;
				Log.info("Data transfered："+(((double)sumL/fileLength)*100)+"%");
				out.write(sendBytes, 0, length);
				out.flush();
			}
			status = in.readByte();

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (status == XConnector.Type.OP_FINISH_FAIL) {
			Log.error("CAddFileTask Upload failed");
	        leaseTask.interrupt();
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
    public void release()
    {
        // TODO Auto-generated method stub

    }

}
