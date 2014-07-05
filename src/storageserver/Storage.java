package storageserver;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileLock;

import common.util.Logger;

public class Storage{
	public static final Logger LOG = Logger.getLogger(Storage.class);

	public static final String STORAGE_DIR_CURRENT = "current";
	public static final String STORAGE_DIR_TRANS = "trans";
	public static final String STORAGE_TMP_REMOVE = "removed.tmp";

	private StorageDirectory storageDir;
	
	public Storage(String location)
	{
		File rootFile = new File(location);
		if(rootFile.exists() == false)
			rootFile.mkdirs();
		storageDir = new StorageDirectory(rootFile);
	}

	public static class StorageDirectory {
		private File root;
		FileLock lock;

		public StorageDirectory(File dir) {
			root = dir;
			lock = null;
		}

		public File getFile() {
			return root;
		}

		public void clearDirectory() throws IOException {
			File curDir = this.getCurrentDir();
			if (curDir.exists()) {
				// TODO 删除目录中所有文件
				// throw new IOException("Cannot remove current directory " +
				// curDir);
			} else if (!curDir.mkdir())
				throw new IOException("Cannot create directory " + curDir);
		}

		public File getCurrentDir() {
			return new File(root, STORAGE_DIR_CURRENT);
		}

		public File getTransDir() {
			return new File(root, STORAGE_DIR_TRANS);
		}

		public File getRemoveTmp() {
			return new File(root, STORAGE_TMP_REMOVE);
		}
	}
}
