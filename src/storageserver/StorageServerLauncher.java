package storageserver;

import java.io.IOException;

import common.util.Logger;

public class StorageServerLauncher {
	private final static Logger logger = Logger
			.getLogger(StorageServerLauncher.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StorageServer storageServer;
		try {
			storageServer = new StorageServer("storage");
			storageServer.initAndstart(5555);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("StorageServer started.");
	}

}
