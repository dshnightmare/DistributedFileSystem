package storageserver;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import common.util.Configuration;
import common.util.FileUtil;
import common.util.Logger;

public class Storage {
	public static final Logger LOG = Logger.getLogger(Storage.class);

	public static final long MAXSTORAGE = Configuration.getInstance().getLong(Configuration.SS_MAX_STORAGE);
	public static final String STORAGE_DIR_CURRENT = "current";
	public static final String STORAGE_DIR_TRANS = "trans";
	public static final String STORAGE_DIR_REMOVE = "removed";

	private StorageDirectory storageDir;

	public Storage(String location) throws IOException {
		File rootFile = new File(location);
		if (rootFile.exists() == false)
			rootFile.mkdirs();
		else {
			if (rootFile.isFile()) {
				rootFile.delete();
				rootFile.mkdir();
			}
		}
		storageDir = new StorageDirectory(rootFile);
	}

	public int analyzeStorageLoad()
	{
		long load = 0;
		for (File file : storageDir.getCurrentDir().listFiles()) {
			if (file.isFile())
				load += file.length();
		}
		
		int t = (int)(load / MAXSTORAGE) * 100;
		if(t > 100)
			t = 100;
		return t;
	}
	public List<String> analyzeCurrentFiles() {
		List<String> files = new ArrayList<String>();
		for (File file : storageDir.getCurrentDir().listFiles()) {
			if (file.isFile())
				files.add(file.getName());
		}
		return files;
	}

	public void removefiles(List<String> files) {
		File curDir = storageDir.getCurrentDir();
		File removeDir = storageDir.getRemoveDir();
		for (String filename : files) {
			File src = new File(curDir.getAbsolutePath() + "//" + filename);
			File dest = new File(removeDir.getAbsolutePath() + "//" + filename);
			if (src.exists())
				src.renameTo(dest);
		}
	}

	public File getTransFile(String name) {
		File file = new File(storageDir.getTransDir().getAbsolutePath() + "//"
				+ name);
		if (file.exists())
			file.delete();
		return file;
	}

	public File getFile(String name) throws IOException{
		File file = new File(storageDir.getCurrentDir().getAbsoluteFile()
				+ "//" + name);
		if(file.exists() == false || file.isFile() == false)
			throw(new IOException("GetFile: file not exist."));
		return file;
	}

	public void transSuccess(String name) throws IOException {
		File curDir = storageDir.getCurrentDir();
		File transDir = storageDir.getTransDir();
		File dest = new File(curDir.getAbsolutePath() + "//" + name);
		File src = new File(transDir.getAbsolutePath() + "//" + name);
		if (src.exists() && src.renameTo(dest))
		{
			
		}
		else
			throw (new IOException("source file not exist."));
	}

	public static class StorageDirectory {
		private File root;
		FileLock lock;

		public StorageDirectory(File dir) throws IOException {
			root = dir;
			File curDir = getCurrentDir();
			File transDir = getTransDir();
			File removeDir = getRemoveDir();
			if (curDir.exists() == false)
				curDir.mkdir();
			else if (curDir.isFile()) {
				curDir.delete();
				curDir.mkdir();
			}
			if (transDir.exists() == false)
				transDir.mkdir();
			else if (transDir.isFile()) {
				transDir.delete();
				transDir.mkdir();
			} else
				clearDirectory(transDir);
			if (removeDir.exists() == false)
				removeDir.mkdir();
			else if (removeDir.isFile()) {
				removeDir.delete();
				removeDir.mkdir();
			} else
				clearDirectory(removeDir);
			lock = null;
		}

		public File getFile() {
			return root;
		}

		public void clearDirectory(File dir) throws IOException {
			// File curDir = this.getCurrentDir();
			// if (curDir.exists()) {
			// // TODO 删除目录中所有文件
			// // throw new IOException("Cannot remove current directory " +
			// // curDir);
			// } else if (!curDir.mkdir())
			// throw new IOException("Cannot create directory " + curDir);
			FileUtil.fullyDeleteContent(dir);
		}

		public File getCurrentDir() {
			return new File(root, STORAGE_DIR_CURRENT);
		}

		public File getTransDir() {
			return new File(root, STORAGE_DIR_TRANS);
		}

		public File getRemoveDir() {
			return new File(root, STORAGE_DIR_REMOVE);
		}
	}
}
