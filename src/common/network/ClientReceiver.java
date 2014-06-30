package common.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import common.observe.call.Call;
import common.util.Configuration;
import common.util.Log;
import common.util.SwitchObjectAndByte;

public class ClientReceiver extends Thread{

	private ClientConnector connector;
	private InputStream in;
	
	public ClientReceiver(ClientConnector _connector, InputStream _in){
		connector = _connector;
		in = _in;
	}

	@Override
	public void run(){
		
		while(true){
			try {
				int buffer_size = Configuration.getInstance().getInteger("ByteBuffer_size");
				byte[] buffer = new byte[buffer_size];
				int received = in.read(buffer);
				System.out.println("Received response: "+received);
				Call response = (Call)SwitchObjectAndByte.switchByteToObject(buffer);
				connector.addResponseCall(response);
				Log.debug("Client received response: "+response.getType());
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
