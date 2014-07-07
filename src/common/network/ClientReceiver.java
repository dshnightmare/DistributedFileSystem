package common.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import common.call.Call;
import common.util.Configuration;
import common.util.Log;
import common.util.SwitchObjectAndByte;

/**
 * holds socket with name server, wait for any data input. read data and translate
 * them into Call
 * @author gengyufeng
 *
 */
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
				Call response = (Call)SwitchObjectAndByte.switchByteToObject(buffer);
				connector.addResponseCall(response);
				Log.debug("Client received response: "+response.getType()+" size:"+received);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}
}
