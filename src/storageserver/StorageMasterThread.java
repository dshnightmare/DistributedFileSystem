package storageserver;

import javax.security.auth.login.Configuration;

import common.observe.call.Call;
import common.observe.event.Event;
import common.observe.event.EventListener;
import common.observe.call.RequestListener;

public class StorageMasterThread implements Runnable, EventListener, RequestListener{
	private final StorageNode node;
	
	
	StorageMasterThread(Configuration conf, StorageNode _node)
	{
		node = _node;
	}
	
	@Override
	public void run() {
	//打开一个端口
	//生成一个网络处理类监听该端口，并注册handler，run
		
	}

	@Override
	public void handleRequest(Call request) {
		// TODO Auto-generated method stub
		StorageTaskThread worker = new StorageTaskThread(0);
		worker.run();
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
}
