package common.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import common.observe.call.Call;
import common.observe.call.CallDispatcher;
import common.observe.call.CallListener;
import common.util.Constant;

/**
 * create an object and @TODO pass callback functions, then call start();
 * @author geng yufeng
 *
 */
public class ServerConnector implements CallDispatcher{

	private int port;
	private BlockingQueue<Call> callQueue;	//calls from client
	private BlockingQueue<Call> responseQueue;	//responses to client

	private List<CallListener> callListeners = new ArrayList<CallListener>();
	
	public ServerConnector(){
		port = Constant.serverPort;	//should be read from conf
		callQueue = new LinkedBlockingDeque<Call>();
		responseQueue = new LinkedBlockingDeque<Call>();
	}
	
	
	public void putCallQueue(Call call){
		/**
		 *  @TODO use callQueue and multithread Handler, or call 
		 *  listeners here?
		 */
		for (CallListener listener : callListeners) {
			listener.handleCall(call);
		}
	}
	
	/**
	 * when completed with a client call, put the response here
	 * @param response
	 */
	public void putResponseQueue(Call response){
		try {
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
}
