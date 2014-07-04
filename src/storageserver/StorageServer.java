package storageserver;


import common.call.Call;
import common.call.CallListener;
import common.call.s2n.RegistrationCallS2N;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.network.ClientConnector;
import common.util.Configuration;

public class StorageServer implements TaskEventListener, CallListener {
	private final static int maxTask = 20;
	private final Storage storage;
	private ClientConnector connector;
	private int taskCount;
	private int taskIDCount;

	StorageServer(String StorageLocation) {
		storage = new Storage();
		taskCount = 0;
		taskIDCount = 0;
		connector = ClientConnector.getInstance();
		connector.addListener(this);
		Configuration conf = Configuration.getInstance();
	}
	
	
	public void start()
	{
		RegistrationCallS2N call =  new RegistrationCallS2N();
		connector.sendCall(call);
 	}
	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub
		// call.getType();
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
