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

public class ServerListener extends Thread{

	private int port;
	private Selector selector;
	
	private ByteBuffer r_buf = ByteBuffer.allocate(1024*8);	//size??
	private ByteBuffer w_buf;
	
	public ServerListener(int _port){
		port = _port;
		try {
			selector = Selector.open();
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
			System.out.println("New connection accepted...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//deal with new call
	private void dealwithRead(SelectionKey key) {
		SocketChannel sc = null;
		try {
			sc = (SocketChannel)key.channel();
			System.out.println("reading data...");
			r_buf.clear();
			//将字节序列从此通道中读入给定的缓冲区r_bBuf
			sc.read(r_buf);
			r_buf.flip();
			String msg = new String(r_buf.array()).toString();
			if(msg.equalsIgnoreCase("1 time")) {
				w_buf = ByteBuffer.wrap(getCurrentTime().getBytes("UTF-8"));
				sc.write(w_buf);
				w_buf.clear();
			} else if(msg.equalsIgnoreCase("1 bye")) {
				sc.write(ByteBuffer.wrap("已经与服务器断开连接".getBytes("UTF-8")));
				sc.socket().close();
			} else {
				sc.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
			}
			System.out.println(msg);
			System.out.println("data done");
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
	
	private String getCurrentTime() {
		Calendar date = Calendar.getInstance();
		String time = "current time: " +
					  date.get(Calendar.YEAR) + "-" +
					  date.get(Calendar.MONTH)+1 + "-" +
					  date.get(Calendar.DATE) + " " +
					  date.get(Calendar.HOUR) + ":" +
					  date.get(Calendar.MINUTE) + ":" +
					  date.get(Calendar.SECOND);
		return time;
	}
}
