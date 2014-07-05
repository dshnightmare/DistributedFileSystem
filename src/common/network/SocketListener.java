package common.network;

import java.net.Socket;

public interface SocketListener {
	public void handleSocket(Socket s);
}
