package test.client;

import nameserver.NameServer;

import client.ClientCMD;
import client.ClientGUI;
import junit.framework.TestCase;

public class TestClientGUI extends TestCase{

	protected void setUp(){
		NameServer ns = new NameServer();
		ns.initilize();
	}
	
	public void testClient(){
		ClientGUI client = new ClientGUI();
		client.init();
		while (true) {
			
		}
	}
}
