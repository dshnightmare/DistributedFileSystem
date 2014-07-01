package common.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import junit.framework.Assert;

import common.observe.call.Call;
import common.observe.call.CallDispatcher;
import common.observe.call.CallListener;
import common.util.Configuration;
import common.util.Constant;
import common.util.Log;
import common.util.Logger;

/**
 * create an object and @TODO pass callback functions, then call start();
 * @author geng yufeng
 *
 */
public class ServerConnector implements CallDispatcher, Connector{

	private volatile static ServerConnector instance = null;
	
	private int port;
	private Configuration cf;
	private BlockingQueue<Call> callQueue;	//calls from client
	private BlockingQueue<Call> responseQueue;	//responses to client
	private Map<String, SocketChannel> channelMap;

	private List<CallListener> callListeners = new ArrayList<CallListener>();
	
	public ServerConnector(){
		cf = Configuration.getInstance();
		port = cf.getInteger("nameserver_port");
		callQueue = new LinkedBlockingDeque<Call>();
		responseQueue = new LinkedBlockingDeque<Call>();
		channelMap = new HashMap<String, SocketChannel>();
	}
	
	public static ServerConnector getInstance(){
		if(null == instance){
			synchronized (ServerConnector.class) {
				instance = new ServerConnector();
				instance.start();
			}
		}
		return instance;
	}
	
	
	public void putCallQueue(Call call){
		/**
		 *  @TODO use callQueue and multithread Handler, or call 
		 *  listeners here?
		 */
		Log.debug("NameServer call looking for listener.");
		for (CallListener listener : callListeners) {
			listener.handleCall(call);
		}
	}
	
	/**
	 * when completed with a client call, put the response here
	 * @param response
	 */
	@Override
	public void sendCall(Call response){
		try {
			System.out.println("Server trying to send response to "+response.getInitiator());
			responseQueue.put(response);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Call getResponse(){
		Call ret = null;
		try {
			ret = responseQueue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * startup utils to deal with network commands
	 */
	public void start(){
		//start listener(accept connection and read data)
		ServerListener sl = new ServerListener(this, port);
		sl.start();
		ServerResponser sr = new ServerResponser(this);
		sr.start();
	}


	@Override
	public void addListener(CallListener listener) {
		// TODO Auto-generated method stub
		callListeners.add(listener);
	}


	@Override
	public void removeListener(CallListener listener) {
		// TODO Auto-generated method stub
		callListeners.remove(listener);
	}
	
	public synchronized void setAddressChannel(String address, SocketChannel channel){
		channelMap.put(address, channel);
	}
	
	public synchronized SocketChannel getChannel(String address){
		assert(channelMap.containsKey(address));
		return channelMap.get(address);
	}
}
