package test.storageserver;

import java.util.concurrent.TimeUnit;

import common.network.ServerConnector;

import nameserver.NameServer;
import storageserver.StorageServer;
import junit.framework.TestCase;

/**
 * 
 * @author dengshihong
 * 
 */
public class TestRegisteration extends TestCase {
	private static ServerConnector connector;
	private static NameServer ns;
	private static StorageServer ss;

	protected void setUp() {
		// connector = ServerConnector.getInstance();
		ns = new NameServer();
		try {
			ns.initilize();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public void testReg() {
		try {
			ss = new StorageServer(
					"/home/lishunyang/workspace/DistributedFileSystem/storage5555");
			ss.initAndstart(5555);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
