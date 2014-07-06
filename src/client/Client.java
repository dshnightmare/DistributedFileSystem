package client;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.task.CAddFileTask;
import client.task.CCreateDirTask;
import client.task.CGetDirectoryTask;
import client.task.CMoveDirectoryTask;
import client.task.CMoveFileTask;
import client.task.CRemoveDirectoryTask;
import client.task.CRemoveFileTask;

import common.call.Call;
import common.call.CallListener;
import common.event.TaskEvent;
import common.event.TaskEventDispatcher;
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
	implements TaskEventListener, TaskEventDispatcher, CallListener{

	private volatile static Client instance;
	
	private TaskMonitor taskMonitor;
	private Map<Long, Task> tasks = new HashMap<Long, Task>();
	private Object taskWaitor = new Object();
    /**
     * List of <tt>TaskEventListener</tt>
     */
    private List<TaskEventListener> listeners =
        new ArrayList<TaskEventListener>();
	
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
		Log.info("getDirectorySync - direct="+direct);
		List<String> ret = new ArrayList<String>();
		CGetDirectoryTask task = new CGetDirectoryTask(IdGenerator.getInstance().getLongId()
				, direct, ret, taskWaitor);
		new Thread(task).start();
		taskMonitor.addTask(task);
		
		synchronized (taskWaitor) {
			try {
				taskWaitor.wait();
			} catch (InterruptedException e) {
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
	public void addFileAsync(String dir, String fileName, File file){
		CAddFileTask task = new CAddFileTask(IdGenerator.getInstance().getLongId()
				, dir, fileName, file);
		new Thread(task).start();
		taskMonitor.addTask(task);
	}
	
	/**
	 * create directory
	 * @param dir full path of new directory
	 */
	public void createDirectoryASync(String dir){
		CCreateDirTask task = new CCreateDirTask(IdGenerator.getInstance().getLongId()
				, dir);
		new Thread(task).start();
		taskMonitor.addTask(task);
	}
	
	/**
	 * remome a file or directory
	 * @param dir
	 * @param name if ends with "/", make dir+name a directory
	 */
	public void removeFileDirectASync(String dir, String name){
		Task task = null;
		if(!name.contains("/")){	//file
			task = new CRemoveFileTask(IdGenerator.getInstance().getLongId()
				, dir, name);
		}
		else {	//directory
			task = new CRemoveDirectoryTask(IdGenerator.getInstance().getLongId()
					, dir+name);
		}
		new Thread(task).start();
		taskMonitor.addTask(task);
	}
	
	public void moveFileDirectASync(String oldDir, String oldName
			,String newDir, String newName){
		Task task = null;
		if(!oldName.contains("/")){	//file
			task = new CMoveFileTask(IdGenerator.getInstance().getLongId()
					, oldDir, oldName, newDir, newName);
		}
		else {
			task = new CMoveDirectoryTask(IdGenerator.getInstance().getLongId()
					, oldDir+oldName, newDir+newName);
		}
		new Thread(task).start();
		taskMonitor.addTask(task);
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
		if(!(event.getTaskThread() instanceof CAddFileTask)
				&&!(event.getTaskThread() instanceof CCreateDirTask)
				&&!(event.getTaskThread() instanceof CRemoveFileTask)
				&&!(event.getTaskThread() instanceof CRemoveDirectoryTask)){
			return;
		}
		synchronized(listeners){
			if (listeners.size() != 0) {
				listeners.get(0).handle(event);
				listeners.remove(0);
			}
		}
	}

	@Override
	public void addListener(TaskEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
	}

	@Override
	public void removeListener(TaskEventListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fireEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
