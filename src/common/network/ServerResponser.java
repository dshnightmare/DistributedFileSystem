package common.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import common.call.Call;
import common.util.Constant;
import common.util.Log;
import common.util.SwitchObjectAndByte;

/**
 * watch over responseQueue of ServerConnector, and send responses(Call)
 * @author gengyufeng
 *
 */
public class ServerResponser extends Thread{

	private ServerConnector connector;
	private ByteBuffer w_buff;
	
	public ServerResponser(ServerConnector _connector){
		connector = _connector;
		w_buff = ByteBuffer.allocate(Constant.ByteBufferSize);
	}
	
	
	@Override
	public void run(){
		while (true) {
			Call resp = connector.getResponse();	//will block here
			SocketChannel sc = connector.getChannel(resp.getInitiator());
			w_buff.clear();
			try {
				w_buff.put(SwitchObjectAndByte.switchObjectToByte(resp));
				w_buff.flip();
				int byteWritten = sc.write(w_buff);
				System.out.println("Server response sent: "+resp.getType()+" size:"+byteWritten);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}
}
