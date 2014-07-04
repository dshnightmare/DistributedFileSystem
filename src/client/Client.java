package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.task.CAddFileTask;
import client.task.CCreateDirTask;
import client.task.CGetDirectoryTask;

import common.call.Call;
import common.call.CallListener;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.Task;
import common.task.TaskMonitor;
import common.util.Configuration;
import common.util.IdGenerator;
import common.util.Log;

/**
 * tasks are generated elsewhere, and addTask() here to start
 * @author gengyufeng
 *
 */
public class Client
	implements TaskEventListener, CallListener{

	private volatile static Client instance;
	
	private TaskMonitor taskMonitor;
	private Map<Long, Task> tasks = new HashMap<Long, Task>();
	private Object taskWaitor = new Object();
	
	public Client(){
		taskMonitor = new TaskMonitor();
		taskMonitor.addListener(this);
	}
	
	public static Client getInstance(){
		if(null == instance){
			synchronized (Client.class) {
				instance = new Client();
			}
		}
		return instance;
	}
	
	/**
	 * RPC call, must block
	 * @param direct target directory
	 * @return
	 */
	public List<String> getDirectorySync(String direct){
		List<String> ret = new ArrayList<String>();
		CGetDirectoryTask task = new CGetDirectoryTask(IdGenerator.getInstance().getLongId()
				, direct, ret, taskWaitor);
		new Thread(task).start();
		taskMonitor.addTask(task);
		
		synchronized (taskWaitor) {
			try {
				taskWaitor.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * RPC call, run in background and will not block
	 * @param dir
	 * @param fileName
	 */
	public void addFileAsync(String dir, String fileName){
		CAddFileTask task = new CAddFileTask(IdGenerator.getInstance().getLongId()
				, dir, fileName);
		new Thread(task).start();
		taskMonitor.addTask(task);
	}
	
	/**
	 * create directory
	 * @param dir full path of new directory
	 */
	public void createDirectorySync(String dir){
		CCreateDirTask task = new CCreateDirTask(IdGenerator.getInstance().getLongId()
				, dir, taskWaitor);
		new Thread(task).start();
		taskMonitor.addTask(task);

		synchronized (taskWaitor) {
			try {
				taskWaitor.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleCall(Call call) {

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
