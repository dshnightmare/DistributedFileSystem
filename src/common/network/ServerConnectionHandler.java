package common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectionHandler extends Thread{

	private Socket clientSocket;
	private ObjectInputStream objIn;
	
	public ServerConnectionHandler(Socket _client){
		clientSocket = _client;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			objIn = new ObjectInputStream(clientSocket.getInputStream());
			try {
				RemoteCommand rc = (RemoteCommand)objIn.readObject();
				String param = "";
				for(int i=0; i<rc.params.length; i++){
					param += " "+rc.params[i];
				}
				System.out.println("[Server]Command recieved: "+param);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
