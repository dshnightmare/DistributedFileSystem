package storageserver;


import javax.security.auth.login.Configuration;

import common.protocol.ClientSServerProtocol;
import common.protocol.InterSServerProtocol;

public class StorageNode implements InterSServerProtocol, ClientSServerProtocol{
	StorageNode(Configuration conf, String StorageLocation)
	{
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StorageNode node = new StorageNode(null, "");
	}
}
