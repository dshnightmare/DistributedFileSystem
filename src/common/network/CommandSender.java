package common.network;

import java.io.ObjectOutputStream;
import java.io.IOException;
import common.observe.call.Call;

/**
 * a connection between client and server(including storage server???)
 * @author geng yufeng
 *
 */
public class CommandSender extends Thread{

	private ClientConnector connector;
	private ObjectOutputStream out;
	
	public CommandSender(ClientConnector _connector, ObjectOutputStream _out){
		connector = _connector;
		out = _out;
	}
	
	@Override
	public void run(){
		
		while(true){
			try {
				Call cmd = connector.getCommandCall();
				out.writeObject(cmd);
				System.out.println("Command sent: "+cmd.callType+cmd.getParamsString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
