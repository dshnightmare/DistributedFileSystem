package common.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import common.util.Constant;

public class ServerConnector {

	private int port;
	private ServerSocket servSocket = null;
	private InputStream in;
	
	public ServerConnector(){
		port = Constant.serverPort;	//should be read from conf
	}
	
	public void setupSocket(){
		try {
			servSocket = new ServerSocket(port);
			
			while(true){
				Socket client = servSocket.accept();
				System.out.println("new socket");
				new ServerConnectionHandler(client);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
