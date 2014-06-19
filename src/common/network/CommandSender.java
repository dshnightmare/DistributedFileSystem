package common.network;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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
				Call cmd = connector.commands.take();
				out.writeObject(cmd);
				System.out.println("Command sent: "+cmd.callType);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
