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
import common.observe.call.CallDispatcher;
import common.observe.call.CallListener;
import common.util.Constant;

/**
 * Client holds an object of this class. 
 * @author geng yufeng
 *
 */
public class ClientConnector extends Thread implements IF_Connector, CallDispatcher{
	
	private Socket socket = null;
	private String remoteIP;
	private int remotePort;
	private BlockingQueue<Call> commands;
	private BlockingQueue<Call> responses;
	
	public ClientConnector(){
		remoteIP = Constant.serverIP;
		remotePort = Constant.serverPort;
		commands = new LinkedBlockingDeque<Call>();
		responses = new LinkedBlockingDeque<Call>();
	}
	
	@Override
	/**
	 * call this method to send a command to nameserver.
	 */
	public void sendCommand(Call command) {
		// TODO Auto-generated method stub
		try {
			commands.put(command);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Call getCommandCall(){
		Call ret = null;
		try {
			ret = commands.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public void addResponseCall(Call resp){
		try {
			responses.put(resp);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * setup connection with server, and create two threads to send and recv respectively
	 */
	public void setupSocket(){
		SocketAddress address = new InetSocketAddress(remoteIP, remotePort);
		while(true){
			try {
				socket = new Socket();
				socket.connect(address, 1000);
			} catch (SocketTimeoutException e) {
				// TODO: handle exception
				System.out.println("Connecting NameServer("+remoteIP+":"+remotePort+") timeout, will reconnect in 5 seconds...");
				try {
					Thread.sleep(5000);
					continue;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			break;
		}
		System.out.println("Connection established with server.");
		try {
			CommandSender cs = new CommandSender(this, new ObjectOutputStream(socket.getOutputStream()));
			cs.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addListener(CallListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(CallListener listener) {
		// TODO Auto-generated method stub
		
	}

}
