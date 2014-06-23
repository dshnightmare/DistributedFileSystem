package common.network;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import common.observe.call.Call;
import common.util.SwitchObjectAndByte;

/**
 * a connection between client and server(including storage server???)
 * @author geng yufeng
 *
 */
public class ClientSender extends Thread{

	private ClientConnector connector;
	private OutputStream out;
	
	public ClientSender(ClientConnector _connector, OutputStream _out){
		connector = _connector;
		out = _out;
	}
	
	@Override
	public void run(){
		
		while(true){
			try {
				Call cmd = connector.getCommandCall();
				out.write(SwitchObjectAndByte.switchObjectToByte(cmd));
//				System.out.println("Command sent: "+cmd.callType+cmd.getParamsString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
