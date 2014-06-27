package common.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.util.Configuration;

public class StorageConnector extends Thread {

	private volatile static StorageConnector instance;

	private int port;
	private Configuration cf;
	private ServerSocket ss;

	public StorageConnector() {
		cf = Configuration.getInstance();
		port = cf.getInteger("nameserver_port");
	}

	public static StorageConnector getInstance(){
		if(null == instance){
			synchronized (StorageConnector.class) {
				instance = new StorageConnector();
				instance.start();
			}
		}
		return instance;
	}
	
	@Override
	public void run() {
		try {
			ss = new ServerSocket(port);
			System.out.println("Server started listing on port:" + port);

			while (true) {
				Socket client = ss.accept();
				StorageConnHandler connHandler = new StorageConnHandler(
						client);
				connHandler.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
