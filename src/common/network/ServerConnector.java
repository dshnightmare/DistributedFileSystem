package common.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import common.util.Constant;

/**
 * create an object and @TODO pass callback functions, then call start();
 * @author geng yufeng
 *
 */
public class ServerConnector extends Thread{

	private int port;
	private ServerSocket servSocket = null;
	
	public ServerConnector(){
		port = Constant.serverPort;	//should be read from conf
	}
	
	@Override
	public void run(){
		setupSocket();
	}
	
	public void setupSocket(){
		try {
			//create server socket, waiting for new clients;
			servSocket = new ServerSocket(port);
			System.out.println("Server started listing on port:"+port);
			while(true){
				Socket client = servSocket.accept();
				ServerConnectionHandler connHandler = new ServerConnectionHandler(client);
				connHandler.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
