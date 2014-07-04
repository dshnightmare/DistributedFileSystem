package test.client;

import nameserver.NameServer;

import client.ClientCMD;
import client.ClientWindow;
import junit.framework.TestCase;

public class TestClientGUI extends TestCase{

	protected void setUp(){
		NameServer ns = new NameServer();
		ns.initilize();
	}
	
	public void testClient(){
		ClientWindow client = new ClientWindow();
		client.init();
		while (true) {
			
		}
	}
}
