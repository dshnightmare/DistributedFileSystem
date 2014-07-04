package test.client;

import nameserver.NameServer;
import common.network.ServerConnector;
import client.ClientCMD;
import junit.framework.TestCase;

public class TestClient extends TestCase{

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
	}
	
	public void testClient(){
		ClientCMD clientCMD = new ClientCMD();
		clientCMD.start();
		while (true) {
			
		}
	}
}
