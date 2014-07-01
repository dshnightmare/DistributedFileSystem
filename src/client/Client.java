package client;

import java.util.HashMap;
import java.util.Map;

import common.network.ClientConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;
import common.thread.TaskThreadMonitor;
import common.util.Configuration;
import common.util.IdGenerator;
import common.util.Log;

public class Client 
	implements TaskEventListener, CallListener{

	private ClientConnector connector;
	private TaskThreadMonitor taskMonitor;
	private Map<Long, TaskThread> tasks = new HashMap<Long, TaskThread>();
	
	public Client(){
		connector = ClientConnector.getInstance();
		taskMonitor = TaskThreadMonitor.getInstance();
		taskMonitor.addListener(this);
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub

        TaskThread task = null;
        long tid = call.getTaskId();
        Configuration conf = Configuration.getInstance();

        if (tid >= 0)
        {
            // Should we send a abort call? Maybe not.
            if (tasks.containsKey(tid))
                tasks.get(tid).handleCall(call);
        }
        else
        {
        	tid = IdGenerator.getInstance().getLongId();
        	
        	Log.debug("Client new task created "+tid+":"+call.getType());
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
