package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import common.network.ClientConnector;
import common.observe.call.AddFileCallC2N;
import common.observe.call.Call;
import common.util.Configuration;
import common.util.Log;

public class ClientCMD extends Thread{

	private ClientConnector connector;
	private Configuration config;
	private String usage;
	
	public ClientCMD(){
		connector = ClientConnector.getInstance();
		config = Configuration.getInstance();
		usage = config.getString("usage");
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
				if(cmdString.equals("addFile")){
					if (args.length != 3) {
						Log.print("Wrong argument number.");
						continue;
					}
					else {
						AddFileCallC2N callC2N = new AddFileCallC2N(args[1], args[2]);
						connector.sendCall(callC2N);
					}
				}
				else if(cmdString.equals("")){
					
				}
				else if(cmdString.equals("")){
					
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
