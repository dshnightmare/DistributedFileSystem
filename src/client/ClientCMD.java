package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import client.task.CAddFileTask;
import client.task.CGetFileTask;
import common.network.ClientConnector;
import common.call.AddFileCallC2N;
import common.call.Call;
import common.call.CallListener;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.TaskLease;
import common.task.Task;
import common.task.TaskMonitor;
import common.util.Configuration;
import common.util.IdGenerator;
import common.util.Log;

public class ClientCMD 
	extends Thread
	implements TaskEventListener, CallListener{

	private ClientConnector connector;
	private Configuration config;
	private String usage;
	
	private Map<Long, Task> tasks = new HashMap<Long, Task>();
	private TaskMonitor taskMonitor;
	
	public ClientCMD(){
		connector = ClientConnector.getInstance();
		config = Configuration.getInstance();
		usage = config.getString("usage");
		
		taskMonitor = TaskMonitor.getInstance();
		taskMonitor.addListener(this);
	}
	
	public void run(){
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));
		while(true){
			Log.print("waiting for command(any input for usage):");
			try {
				String inputLine = reader.readLine();
				String[] args = inputLine.split(" ");
				String cmdString = args[0];
				
				Task task = null;
				
				if(cmdString.toLowerCase().equals("addfile")){
					if (args.length != 3) {
						Log.print("Wrong argument number.");
						continue;
					}
					else {
						task = new CAddFileTask(IdGenerator.getInstance().getLongId()
								, args[1], args[2]);
					}
				}
				else if(cmdString.toLowerCase().equals("getfile")){
					if (args.length != 3) {
						Log.print("Wrong argument number.");
						continue;
					}
					else {
						task = new CGetFileTask(IdGenerator.getInstance().getLongId()
								, args[1], args[2]);
					}
				}
				else if(cmdString.toLowerCase().equals("")){
					
				}
				else {
					Log.print(usage);
				}
				if(null != task){
					synchronized (tasks)
	                {
	                    tasks.put(task.getTaskId(), task);
	                }

	                taskMonitor.addThread(task);
	                new Thread(task).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.info("Read command failed, please try again.");
			}
		}
	}

	@Override
	public void handleCall(Call call) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}
}
