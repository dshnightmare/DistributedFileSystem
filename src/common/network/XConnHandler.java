package common.network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.observe.call.Call;
import common.util.Constant;

/**
 * deals with connection with one client.
 * @TODO how to disconnect?
 * @author geng yufeng
 *
 */
public class XConnHandler extends Thread{

	private Socket clientSocket;
	private DataInputStream inputStream;
	
	public XConnHandler(Socket _client){
		clientSocket = _client;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				inputStream = new DataInputStream(
						new BufferedInputStream(
								clientSocket.getInputStream()));
				
				byte op = inputStream.readByte();
				
				switch (op) {
				case Constant.READ_FILE:
					
					break;

				case Constant.WRITE_FILE:
					
					break;
					
				default:
					System.out.println("Wrong Op code!");
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}

}
