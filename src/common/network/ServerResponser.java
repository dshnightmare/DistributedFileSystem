package common.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import common.observe.call.Call;
import common.util.Constant;
import common.util.SwitchObjectAndByte;

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
			SocketChannel sc = resp.getChannel();
			w_buff.clear();
			try {
				w_buff.put(SwitchObjectAndByte.switchObjectToByte(resp));
				sc.write(w_buff);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
