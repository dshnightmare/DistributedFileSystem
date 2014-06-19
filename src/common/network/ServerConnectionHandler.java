package common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectionHandler implements Runnable{

	private Socket clientSocket;
	private DataOutputStream out;
	private ObjectInputStream objIn;
	
	public ServerConnectionHandler(Socket _client){
		clientSocket = _client;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			objIn = new ObjectInputStream(clientSocket.getInputStream());
			try {
				RemoteCommand rc = (RemoteCommand)objIn.readObject();
				String param = rc.params[0]+rc.params[1];
				
				out.writeUTF(param);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			out.close();
			objIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(clientSocket != null){
				try {
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
