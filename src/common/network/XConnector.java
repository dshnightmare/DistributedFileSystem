package common.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.PUBLIC_MEMBER;

import common.call.Call;
import common.call.CallDispatcher;
import common.call.CallListener;
import common.util.Configuration;
import common.util.Logger;

public class XConnector 
	extends Thread
	implements SocketDispatcher{

	private int port = 0;
	private Configuration cf;
	private ServerSocket ss;
	
	private List<SocketListener> socketListeners = new ArrayList<SocketListener>();

	public XConnector(int port) {
		cf = Configuration.getInstance();
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			ss = new ServerSocket(port);
			System.out.println("XConnector started listening on port:" + port);

			while (true) {
				Socket client = ss.accept();
				for(SocketListener listener:socketListeners){
					listener.handleSocket(client);
				}
//				XConnHandler connHandler = new XConnHandler(client);
//				connHandler.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * create socket
	 * @param ip	ip
	 * @param port	port
	 * @return
	 */
	public Socket getSocket(String ip, int port){
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
			System.out.println("XConnector connected to"
			+socket.getRemoteSocketAddress().toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket;
	}
	
	/*
	 * defines operation code for xconnection
	 */
	public static class Type{
		/*
		 * 
		 */
		public static final byte OP_READ_BLOCK = 0;
		/*
		 * 
		 */
		public static final byte OP_WRITE_BLOCK = 1;
		/*
		 * load balance
		 */
		public static final byte OP_REPLACE_BLOCK = 0;
	}

	@Override
	public void addSocketListener(SocketListener listener) {
		// TODO Auto-generated method stub
		synchronized (socketListeners) {
			socketListeners.add(listener);
		}
	}

	@Override
	public void removeSocketListener(SocketListener listener) {
		// TODO Auto-generated method stub
		synchronized (socketListeners) {
			socketListeners.remove(listener);
		}
	}
}
