package common.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * a connection between client and server(including storage server???)
 * @author geng yufeng
 *
 */
public class Connection extends Thread{

	private Socket socket = null;
	private String remoteIP;
	private int remotePort;
	private InputStream in;
	private OutputStream out;
	
	public Connection(String _remoteIP, int _remotePort){
		remoteIP = _remoteIP;
		remotePort = _remotePort;
	}
	
	public void setupConnection(){
		try {
			socket = new Socket(remoteIP, remotePort);
			out = socket.getOutputStream();
			out.write("kdjslfjs".getBytes());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
