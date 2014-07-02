package client;

import java.util.HashMap;
import java.util.Map;

import common.network.ClientConnector;
import common.call.Call;
import common.call.CallListener;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.Task;
import common.task.TaskMonitor;
import common.util.Configuration;
import common.util.IdGenerator;
import common.util.Log;

public class Client 
	implements TaskEventListener, CallListener{

	private ClientConnector connector;
	private TaskMonitor taskMonitor;
	private Map<Long, Task> tasks = new HashMap<Long, Task>();
	
	public Client(){
		connector = ClientConnector.getInstance();
		taskMonitor = TaskMonitor.getInstance();
		taskMonitor.addListener(this);
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub

        Task task = null;
        long remoteTaskId = call.getFromTaskId();
        long localTaskId = call.getToTaskId();
        Configuration conf = Configuration.getInstance();

        if (localTaskId >= 0)
        {
            // Should we send a abort call? Maybe not.
            if (tasks.containsKey(localTaskId))
                tasks.get(localTaskId).handleCall(call);
        }
        else
        {
        	localTaskId = IdGenerator.getInstance().getLongId();
        	
        	Log.debug("Client new task created "+localTaskId+":"+call.getType());
        	if(Call.Type.ADD_FILE_N2C == call.getType()){
        		//update file to master storage server listed
        	}
        	else {
				Log.print("Wrong command type!"+call.getType());
			}
        }
	}

	@Override
	public void handle(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
