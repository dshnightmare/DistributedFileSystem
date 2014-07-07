package common.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import common.call.Call;
import common.call.CallDispatcher;
import common.call.CallListener;
import common.call.all.AbortCall;
import common.util.Configuration;
import common.util.Constant;
import common.util.Log;
import common.util.SwitchObjectAndByte;

/**
 * every client need an instance of ClientConnector to Name Server
 * </b>
 * ClientConnector sends calls to name server, and collects responses. calls and responses
 * are defined in package common.call
 * </b>
 * use sendCall(Call) to send a Call
 * </b>
 * implement CallListener and add listener to ClientConnector to deal with responses
 * @author geng yufeng
 *
 */
public class ClientConnector implements Connector, CallDispatcher{
	
	private volatile static ClientConnector instance;
	/**
	 * socket created to connect with name server
	 */
	private Socket socket = null;
	/**
	 * name server ip/port
	 */
	private String remoteIP;
	private int remotePort;
	private Configuration cf;
	/**
	 * list of command to be sent to name server
	 */
	private BlockingQueue<Call> commands;
	/**
	 * list of responses received form name server
	 */
	private BlockingQueue<Call> responses;
	/**
	 * listeners for response add to clientConnector
	 */
	private List<CallListener> responseListeners = new ArrayList<CallListener>();
	
	public ClientConnector(){
		cf = Configuration.getInstance();
		remoteIP = cf.getString("nameserver_ip");
		remotePort = cf.getInteger("nameserver_port");
		commands = new LinkedBlockingDeque<Call>();
		responses = new LinkedBlockingDeque<Call>();
	}
	
	/**
	 * get a globally unique ClientConnector instance
	 * @return
	 */
	public static ClientConnector getInstance(){
		if(null == instance){
			synchronized (ClientConnector.class) {
				instance = new ClientConnector();
				instance.setupSocket();
			}
		}
		return instance;
	}
	
	@Override
	/**
	 * call this method to send a command to nameserver.
	 */
	public void sendCall(Call command) {
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
	
	/**
	 * receive response from server
	 * @param resp
	 */
	public void addResponseCall(Call resp){
		try {
			responses.put(resp);
			if (resp.getType() == Call.Type.ABORT) {
				Log.error(((AbortCall)resp).getReason());
			}
			for(CallListener listener : responseListeners){
				listener.handleCall(resp);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void addListener(CallListener listener) {
		// TODO Auto-generated method stub
		responseListeners.add(listener);
	}

	@Override
	public synchronized void removeListener(CallListener listener) {
		// TODO Auto-generated method stub
		responseListeners.remove(listener);
	}
	
	/**
	 * return local address of socket used.
	 * format: "/X.X.X.X:X"
	 * @return
	 */
	public String getLocalAddress(){
		return socket.getLocalSocketAddress().toString();
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
		Log.info("Connection established with:"+socket.getRemoteSocketAddress().toString());
		try {
			ClientSender cs = new ClientSender(this, socket.getOutputStream());
			cs.start();
			ClientReceiver cr = new ClientReceiver(this, socket.getInputStream());
			cr.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
