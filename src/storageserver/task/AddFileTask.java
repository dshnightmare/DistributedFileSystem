package storageserver.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.Socket;

import storageserver.Storage;
import common.call.Call;
import common.network.XConnector;
import common.util.Logger;

public class AddFileTask extends StorageServerTask {
	private final static Logger logger = Logger.getLogger(AddFileTask.class);
	private final Socket socket;
	private final Storage storage;
	private final DataInputStream dis;
	
	private Object waitor = new Object();

	public AddFileTask(long tid, Socket socket, DataInputStream dis, Storage storage) {
		super(tid);
		this.socket = socket;
		this.dis = dis;
		this.storage = storage;
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		String filename;
		long length;
		int todo;
		String[] todoAddress = null;
		int toreadlen;
		int readlen;
		byte[] inputByte = null;
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			try {
				todo = dis.readInt();
				if(todo != 0)
				{
					todoAddress = new String[todo];
					for(int i = 0; i < todo; i++)
						todoAddress[i] = dis.readUTF();
				}
				filename = dis.readUTF();
				length = dis.readLong();
				inputByte = new byte[1024];
				fos = new FileOutputStream(storage.getTransFile(filename));
				dos = new DataOutputStream(socket.getOutputStream());
				toreadlen = (length < inputByte.length) ? (int)length : inputByte.length;
				while(length > 0 && (readlen = dis.read(inputByte, 0, toreadlen)) > 0){
					fos.write(inputByte, 0, readlen);
					fos.flush();
					length -=  readlen;
					toreadlen = (length < inputByte.length) ? (int)length : inputByte.length;
				}
				if(length < 0)
					throw(new Exception("AddFileTask: filelength errors."));
//				fireEvent(event);
//				synchronized (waitor) {
//					waitor.wait();
//				}
				storage.transSuccess(filename);
				dos.writeByte(XConnector.Type.OP_FINISH_SUC);
					
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("AddFileTask failed");
				if(dos != null)
					dos.writeByte(XConnector.Type.OP_FINISH_FAIL);
			}
			finally{
				if(fos != null)
					fos.close();
				if(dis != null)
					dis.close();
				if(dos != null)
					dos.close();
				if(socket != null)
					socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setFinish();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

}
