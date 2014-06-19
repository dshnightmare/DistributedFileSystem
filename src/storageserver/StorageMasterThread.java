package storageserver;

import java.net.Socket;

import common.observe.event.Event;
import common.observe.event.EventListener;
import common.observe.request.Request;
import common.observe.request.RequestListener;

public class StorageMasterThread implements Runnable, EventListener, RequestListener{
	
	@Override
	public void run() {
	//打开一个端口
	//生成一个网络处理类监听该端口，并注册handler，run
		
	}

	@Override
	public void handleRequest(Request request) {
		// TODO Auto-generated method stub
		StorageTaskThread worker = new StorageTaskThread(0);
		worker.run();
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
}
