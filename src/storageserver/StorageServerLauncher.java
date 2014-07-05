package storageserver;

import common.util.Logger;

public class StorageServerLauncher {
	private final static Logger logger = Logger
			.getLogger(StorageServerLauncher.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StorageServer storageServer = new StorageServer("storage");

		try {
			storageServer.initAndstart(5555);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("StorageServer started.");
	}

}
