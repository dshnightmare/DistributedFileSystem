package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import client.task.CAddFileTask;
import client.task.CGetFileTask;
import common.task.Task;
import common.util.Configuration;
import common.util.IdGenerator;
import common.util.Log;

public class ClientCMD 
	extends Thread{

	private Configuration config;
	private Client client;
	private String usage;
	
	public ClientCMD(){
		config = Configuration.getInstance();
		usage = config.getString("usage");
		client = Client.getInstance();
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
						client.addTask(task);
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
						client.addTask(task);
					}
				}
				else if(cmdString.toLowerCase().equals("")){
					
				}
				else {
					Log.print(usage);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.info("Read command failed, please try again.");
			}
		}
	}
}
