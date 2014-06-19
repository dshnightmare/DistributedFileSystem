package common.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ServerConnectionHandler implements Runnable{

	private Socket clientSocket;
	private DataInputStream in;
	
	public ServerConnectionHandler(Socket _client){
		clientSocket = _client;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			in = new DataInputStream(clientSocket.getInputStream());
			String inString = in.readUTF();
			System.out.println("From client: "+inString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
