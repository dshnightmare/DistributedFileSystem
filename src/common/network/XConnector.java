package common.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Map;

import org.omg.CORBA.PUBLIC_MEMBER;

import common.observe.call.Call;
import common.observe.call.CallDispatcher;
import common.observe.call.CallListener;
import common.util.Configuration;
import common.util.Logger;

public class XConnector extends Thread implements Connector, CallDispatcher{

	private volatile static XConnector instance;

	private int port;
	private Configuration cf;
	private ServerSocket ss;
	
	private Map<String, Socket> socketMap;

	public XConnector() {
		cf = Configuration.getInstance();
		port = cf.getInteger("xconnector_port");
	}

	public static XConnector getInstance(){
		if(null == instance){
			synchronized (XConnector.class) {
				instance = new XConnector();
				instance.start();
			}
		}
		return instance;
	}
	
	@Override
	public void run() {
		try {
			ss = new ServerSocket(port);
			System.out.println("XConnector started listening on port:" + port);

			while (true) {
				Socket client = ss.accept();
				putSocket(client.getRemoteSocketAddress().toString(), client);
				XConnHandler connHandler = new XConnHandler(
						client);
				connHandler.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void putSocket(String id, Socket socket){
		socketMap.put(id, socket);
	}
	
	/**
	 * @TODO synchronized may have problem here!
	 * @param id SO.getRemoteSocketAddress().toString()[/127.0.0.1:5356]
	 * @return
	 */
	public synchronized Socket getSocket(String id){
		Socket socket = null;
		if(socketMap.containsKey(id))
			socket = socketMap.get(id);
		else {
			if (id.substring(0, 1).equals("/")) {
				id = id.substring(1);
			}
			String host = id.split(":")[0];
			int port = Integer.parseInt(id.split(":")[1]);
			try {
				socket = new Socket(host, port);
				System.out.println("XConnector connected to"+id);
				//@TODO dead lock?
				putSocket(id, socket);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return socket;
	}

	@Override
	public void addListener(CallListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(CallListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * may not be used by storage server
	 */
	public void sendCall(Call command) {
		// TODO Auto-generated method stub
		
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
}
