package storageserver;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileLock;

import common.util.Logger;

public class Storage extends StorageInfo {
	public static final Logger LOG = Logger.getLogger(Storage.class);

	public static final String STORAGE_DIR_CURRENT = "current";
	public static final String STORAGE_DIR_PREVIOUS = "previous";
	public static final String STORAGE_TMP_REMOVE = "removed.tmp";

	public enum StorageState {
		NORMAL;
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
		
		public File getPreviousDir(){
			return new File(root, STORAGE_DIR_PREVIOUS);
		}
		
		public File getRemoveTmp(){
			return new File(root, STORAGE_TMP_REMOVE);
		}
		
		public StorageState analyzeStorage() throws IOException{
			assert root != null;
			String rootPath = root.getCanonicalPath();
//			try{
//				if(!root.exists()){
//					LOG.warn("storage");
//				}
//			}
			return StorageState.NORMAL;
		}
	}

	private StorageDirectory storageDir;
}
