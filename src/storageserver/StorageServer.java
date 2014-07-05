package storageserver;


import java.util.HashMap;
import java.util.Map;

import common.call.Call;
import common.call.CallListener;
import common.call.n2s.MigrateFileCallN2S;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.network.ClientConnector;
import common.task.Task;
import common.util.Configuration;
import common.util.Logger;

public class StorageServer implements TaskEventListener, CallListener {
	private static final Logger logger = Logger.getLogger(StorageServer.class);
	private final Storage storage;
	private ClientConnector connector;
	private Map<Long, Task> tasks = new HashMap<Long, Task>();
	private int taskIDCount;

	StorageServer(String location) {
		Configuration conf = Configuration.getInstance();
		storage = new Storage(location);
		taskIDCount = 0;
		connector = ClientConnector.getInstance();
		connector.addListener(this);
		
	}
	
	public void initAndstart(){
		
	}
	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub
		switch (call.getType()) 
		{
		// TODO 需要添加注册成功之后的处理
		case MIGRATE_FILE_N2S:
		{
			MigrateFileCallN2S handlecall = (MigrateFileCallN2S)call;
			for (String key: handlecall.getFiles().keySet()) {
			}
		}
		case SYNC_N2S:
			break;
		default:
			break;
		}
		StorageTaskThread task = new StorageTaskThread(taskIDCount++);
		task.init(call, storage);
		// add listener
		task.run();
	}

	@Override
	public void handle(TaskEvent event) {
		// TODO Auto-generated method stub

	}
}
