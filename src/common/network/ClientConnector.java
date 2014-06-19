package common.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import common.observe.call.Call;
import common.util.Constant;

/**
 * Client holds an object of this class. 
 * @author geng yufeng
 *
 */
public class ClientConnector extends Thread implements IF_Connector{
	
	private Socket socket = null;
	private String remoteIP;
	private int remotePort;
	public BlockingQueue<Call> commands;
	
	public ClientConnector(){
		remoteIP = Constant.serverIP;
		remotePort = Constant.serverPort;
		commands = new LinkedBlockingDeque<Call>();
	}
	
	@Override
	public void sendCommand(Call command) {
		// TODO Auto-generated method stub
		try {
			commands.put(command);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * setup connection with server, and create two threads to send and recv respectively
	 */
	public void setupSocket(){
		socket = new Socket();
		SocketAddress address = new InetSocketAddress(remoteIP, remotePort);
		while(true){
			try {
				socket.connect(address, 1000);
			} catch (SocketTimeoutException e) {
				// TODO: handle exception
				System.out.println("Connecting to NameServer timeout, will reconnect in 10 seconds...");
				try {
					Thread.sleep(10000);
					continue;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		System.out.println("Connection established with server.");
		try {
			(new CommandSender(this, new ObjectOutputStream(socket.getOutputStream()))).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
