package test.client;

import storageserver.StorageServer;
import nameserver.NameServer;
import client.ClientCMD;
import client.ClientGUI;
import junit.framework.TestCase;

public class TestClientGUI extends TestCase{

	protected void setUp(){
		NameServer ns = new NameServer();
		try
        {
            ns.initilize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

		try {
			StorageServer ss = new StorageServer("D:/dshsb");
			ss.initAndstart(5555);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void testClient(){
		ClientGUI client = new ClientGUI();
		client.init();
		while (true) {
			
		}
	}
}
