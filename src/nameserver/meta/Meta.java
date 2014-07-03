package nameserver.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Meta data structure of <tt>NameServer</tt>. Meta data includes information of
 * directories or files such as name, version, id, and so on.
 * <p>
 * <strong>Warning: This structure is thread unsafe.</strong>
 * 
 * @author lishunyang
 * @see NameServer
 * @see Directory
 * @see File
 */
public class Meta {
	/**
	 * Single instance of Meta data.
	 */
	private static Meta instance = new Meta();

	/**
	 * Directory maps.
	 * <p>
	 * {directory name, directory}
	 */
	private Map<String, Directory> directories = new HashMap<String, Directory>();

	/**
	 * File maps.
	 * <p>
	 * {file id, file}
	 */
	private Map<String, File> files = new HashMap<String, File>();

	/**
	 * Construction method.
	 */
	private Meta() {
	}

	/**
	 * Get single instance.
	 * 
	 * @return
	 */
	public static Meta getInstance() {
		return instance;
	}

	/**
	 * Reset <tt>Meta</tt> structure.
	 */
	public synchronized void clear() {
		directories.clear();
		files.clear();
	}

	/**
	 * Get specified directory by name.
	 * 
	 * @param dirName
	 * @return null if the directory doesn't exist.
	 */
	public Directory getDirectory(String dirName) {
		// Directory doesn't exist.
		if (!containDirectory(dirName))
			return null;

		// Directory does exist but we haven't created it yet.(lazy creation)
		Directory dir = directories.get(dirName);
		if (null == dir) {
			dir = new Directory(dirName);
			// We assume that the directory is valid.
			dir.setValid(true);
			directories.put(dirName, dir);
		}

		return dir;
	}

	/**
	 * Add new directory.
	 * 
	 * @param directory
	 */
	public void addDirectory(Directory directory) {
		// Lazy Creation: As for a new directory, if its parent directories
		// don't exist, we just add new directory and ignore its parent
		// directories. Those parent directories are going to be
		// created when we actually need them.
		directories.put(directory.getName(), directory);
	}

	/**
	 * Remove a specified directory, include all files or directories among it.
	 * 
	 * @param dirName
	 * @return
	 */
	public List<Directory> removeDirectory(String dirName) {
		List<Directory> removedDirs = getDirectoriesByPrefix(dirName);

		for (Directory dir : removedDirs) {
			directories.remove(dir.getName());
		}

		return removedDirs;
	}

	/**
	 * Test whether a specified directory exists.
	 * 
	 * @param dirName
	 * @return
	 */
	public boolean containDirectory(String dirName) {
		return !getDirectoriesByPrefix(dirName).isEmpty();
	}

	/**
	 * Test whether a specified directory is valid.
	 * <p>
	 * 
	 * @param dirName
	 * @return
	 */
	public boolean isDirectoryValid(String dirName) {
		Directory dir = getDirectory(dirName);
		if (null == dir)
			return false;
		return dir.isValid();
	}

	/**
	 * Set the valid bit of specified directory.
	 * 
	 * @param dirName
	 * @param valid
	 */
	public void setDirectoryValid(String dirName, boolean valid) {
		Directory dir = getDirectory(dirName);
		if (null == dir)
			return;
		dir.setValid(valid);
	}

	/**
	 * Rename a specified directory, include all files and directories among it.
	 * 
	 * @param oldDirName
	 * @param newDirName
	 */
	public void renameDirectory(String oldDirName, String newDirName) {
		List<Directory> relatedDirs = removeDirectory(oldDirName);

		if (relatedDirs.isEmpty())
			return;

		for (Directory dir : relatedDirs) {
			dir.setName(dir.getName().replaceFirst(oldDirName, newDirName));
			addDirectory(dir);
		}
	}

	/**
	 * Get a specified file by directory name and file name.
	 * 
	 * @param dirName
	 * @param fileName
	 * @return null if the file dosn't exist.
	 */
	public File getFile(String dirName, String fileName) {
		final Directory dir = getDirectory(dirName);

		if (null == dir)
			return null;
		return dir.getFile(fileName);
	}

	public File getFile(Long fid) {
		return files.get(fid);
	}

	/**
	 * Add a file to meta structure.
	 * <p>
	 * If there already has the same file, it will be replaced.
	 * 
	 * @param dirName
	 * @param file
	 *            The file that you want to added.
	 */
	public void addFile(String dirName, File file) {
		Directory dir = getDirectory(dirName);
		if (null == dir) {
			dir = new Directory(dirName);
			addDirectory(dir);
		}
		dir.addFile(file);
		files.put(file.getId(), file);
	}

	/**
	 * Remove a specified file from meta structure.
	 * <p>
	 * Do nothing if the file dosn't exist.
	 * 
	 * @param dirName
	 * @param fileName
	 */
	public void removeFile(String dirName, String fileName) {
		Directory dir = getDirectory(dirName);
		if (null == dir)
			return;
		File file = dir.removeFile(fileName);
		if (null != file)
			files.remove(file.getId());
	}

	/**
	 * Test whether a file with specified name does exist in meta structure.
	 * 
	 * @param dirName
	 * @param fileName
	 * @return
	 */
	public boolean containFile(String dirName, String fileName) {
		final File file = getFile(dirName, fileName);

		return null != file;
	}

	/**
	 * Test whether a file with specified id does exist in meta structure.
	 * 
	 * @param fid
	 * @return
	 */
	public boolean containFile(String fid) {
		final File file = files.get(fid);

		return null != file;
	}

	/**
	 * Test whether the a file with specified name is valid.
	 * 
	 * @param dirName
	 * @param fileName
	 * @return
	 */
	public boolean isFileValid(String dirName, String fileName) {
		File file = getFile(dirName, fileName);
		if (null == file)
			return false;
		return file.isValid();
	}

	/**
	 * Set valid bit of a file with specified name.
	 * 
	 * @param dirName
	 * @param fileName
	 * @param valid
	 */
	public void setFileValid(String dirName, String fileName, boolean valid) {
		File file = getFile(dirName, fileName);
		if (null == file)
			return;
		file.setValid(valid);
	}

	/**
	 * Rename a specified file.
	 * 
	 * @param oldDirName
	 * @param oldFileName
	 * @param newDirName
	 * @param newFileName
	 */
	public void renameFile(String oldDirName, String oldFileName,
			String newDirName, String newFileName) {
		Directory oldDir = getDirectory(oldDirName);
		if (null == oldDir)
			return;
		File file = oldDir.removeFile(oldFileName);
		if (null == file)
			return;
		file.setName(newFileName);
		addFile(newDirName, file);
	}

	/**
	 * Get all directories in this meta structure.
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getValidDirectoryList() {
		Map<String, Map<String, String>> dirList = new HashMap<String, Map<String, String>>();

		for (Directory dir : directories.values()) {
			if (dir.isValid())
				dirList.put(dir.getName(), dir.getValidFileNameList());
		}
		return dirList;
	}

	/**
	 * Get sub-directory name of a specified directory.
	 * 
	 * @param currentDirName
	 */
	public List<String> getSubDirectoryName(String currentDirName) {
		List<Directory> subDirs = getDirectoriesByPrefix(currentDirName);
		List<String> result = new ArrayList<String>();

		for (Directory dir : subDirs) {
			if (dir.getName().matches(currentDirName + ".*/.+"))
				continue;
			result.add(dir.getName().substring(currentDirName.length()));
		}

		return result;
	}

	/**
	 * Get all directories which have same name prefix.
	 * 
	 * @param dirNamePrefix
	 * @return
	 */
	private List<Directory> getDirectoriesByPrefix(String dirNamePrefix) {
		List<Directory> result = new ArrayList<Directory>();

		for (Entry<String, Directory> e : directories.entrySet()) {
			if (e.getKey().matches(dirNamePrefix + ".*")) {
				result.add(e.getValue());
			}
		}

		return result;
	}
}
