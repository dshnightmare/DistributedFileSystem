package common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.observe.call.Call;

/**
 * deals with connection with one client.
 * @TODO how to disconnect?
 * @author geng yufeng
 *
 */
public class ServerConnectionHandler extends Thread{

	private Socket clientSocket;
	private ObjectInputStream objIn;
	
	public ServerConnectionHandler(Socket _client){
		clientSocket = _client;
		try {
			objIn = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				Call rc = (Call)objIn.readObject();
				String param = "";
				for(int i=0; i<rc.params.length; i++){
					param += " "+rc.params[i];
				}
				System.out.println("[Server]Command recieved: "+rc.callType+" "+param);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}

}
