package common.network;

import java.io.IOException;
import java.io.ObjectInputStream;

import common.observe.call.Call;

public class CommandReceiver extends Thread{

	private ClientConnector connector;
	private ObjectInputStream in;
	
	public CommandReceiver(ClientConnector _connector, ObjectInputStream _in){
		connector = _connector;
		in = _in;
	}

	@Override
	public void run(){
		
		while(true){
			try {
				Call response = (Call)in.readObject();
				connector.addResponseCall(response);
				System.out.println("Response: "+response.callType+response.getParamsString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
