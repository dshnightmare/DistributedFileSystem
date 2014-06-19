package common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import common.util.Constant;

public class ClientConnector {
	
	private Socket socket = null;
	private String remoteIP;
	private int remotePort;
	private DataInputStream in;
	private ObjectOutputStream objOut;
	
	public ClientConnector(){
		remoteIP = Constant.serverIP;
		remotePort = Constant.serverPort;
	}
	
	public void setupSocket(){
		try {
			socket = new Socket(remoteIP, remotePort);

			String[] params = {"lsdjl", "123"};
			RemoteCommand rc = new RemoteCommand(3, params);
			objOut = new ObjectOutputStream(socket.getOutputStream());
			objOut.writeObject(rc);
			
			String ret1 = in.readUTF();
			System.out.println("server reply is: "+ret1);
			
			objOut.close();
			in.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
