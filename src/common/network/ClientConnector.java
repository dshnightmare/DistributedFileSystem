package common.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnector {
	
	private Socket socket = null;
	private String remoteIP;
	private int remotePort;
	private InputStream in;
	private OutputStream out;
	
	public ClientConnector(){
		remoteIP = NetworkConstant.serverIP;
		remotePort = NetworkConstant.serverPort;
	}
	
	public void setupConnection(){
		try {
			socket = new Socket(remoteIP, remotePort);
			out = socket.getOutputStream();
			out.write("kdjslfjs".getBytes());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
