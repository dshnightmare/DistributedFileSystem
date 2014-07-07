package common.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Scanner;

import common.call.Call;
import common.util.Configuration;
import common.util.Constant;
import common.util.Log;
import common.util.SwitchObjectAndByte;

/**
 * listen on given port, accept connection or read data
 * @author gengyufeng
 *
 */
public class ServerListener extends Thread{

	private int port;
	private Configuration cf;
	private ServerConnector connector;
	private Selector selector;
	
	private ByteBuffer r_buf;
	private ByteBuffer w_buf;
	
	/**
	 * setup ServerListener
	 * @param _connector	server connector it belons to
	 * @param _port	port to listen
	 */
	public ServerListener(ServerConnector _connector, int _port){
		port = _port;
		connector = _connector;
		try {
			selector = Selector.open();
			cf = Configuration.getInstance();
			r_buf = ByteBuffer.allocate(cf.getInteger("ByteBuffer_size"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ssc.socket().bind(new InetSocketAddress(port));
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server listening on port:"+port);
			
			while(true){
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while(it.hasNext()){
					SelectionKey key = it.next();
					if(key.isAcceptable()){
						dealwithAccept(key);
					}

					if(key.isReadable()){
						dealwithRead(key);
					}
					it.remove();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//deal with new connection
	private void dealwithAccept(SelectionKey key) {
		try {
			System.out.println("New connection received...");
			ServerSocketChannel server = (ServerSocketChannel)key.channel();
			SocketChannel sc = server.accept();
			sc.configureBlocking(false);
			//注册读事件
			sc.register(selector, SelectionKey.OP_READ);
			connector.setAddressChannel(sc.socket().getRemoteSocketAddress().toString(), sc);
			Log.info("New connection accepted...("+sc.socket().getRemoteSocketAddress().toString()+")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//deal with new call
	private void dealwithRead(SelectionKey key) {
		SocketChannel sc = null;
		try {
			sc = (SocketChannel)key.channel();
			r_buf.clear();
			//read into r_bBuf
			int byteRead = sc.read(r_buf);
			r_buf.flip();
			try {
				//establish a Call object and bind the socketchannel
				Call rc = (Call)SwitchObjectAndByte.switchByteToObject(r_buf.array());
				rc.setInitiator(sc.socket().getRemoteSocketAddress().toString());
				connector.putCallQueue(rc);
				Log.debug("NameServer received call: "+rc.getType()+" size:"+byteRead);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			r_buf.clear();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				sc.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
