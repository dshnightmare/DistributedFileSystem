package common.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;

public class ServerConnector {

	private int port;
	private ServerSocket servSocket = null;
	private InputStream in;
	
	public ServerConnector(){
		port = NetworkConstant.serverPort;	//should be read from conf
	}
	
	public void setupSocket(){
		try {
			servSocket = new ServerSocket(port);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
