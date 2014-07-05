package common.network;

public interface SocketDispatcher {

	public void addSocketListener(SocketListener listener);
	public void removeSocketListener(SocketListener listener);
}
