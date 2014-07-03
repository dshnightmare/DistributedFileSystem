package nameserver.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Storage server meta data.
 * 
 * @author lishunyang
 * 
 */
public class Storage {
	// FIXME: It seems that we don't need this.
	/**
	 * storage server id.
	 */
	private final long id;

	/**
	 * Storage server's communication address.
	 * <p>
	 * ip address + port
	 */
	private final String address;

	/**
	 * Heatbeat timestamp.
	 */
	private long heartbeatTime;

	/**
	 * Percentage of storage load.
	 */
	private int load;

	/**
	 * Files that are contained by this storage server.
	 */
	private List<File> files = new ArrayList<File>();

	/**
	 * Files that need to be migrated from other storage server to local storage
	 * server.
	 */
	private Map<Storage, List<File>> migrateFiles = new HashMap<Storage, List<File>>();

	/**
	 * Construction method.
	 * 
	 * @param id
	 * @param address
	 */
	public Storage(long id, String address) {
		this.id = id;
		this.address = address;
	}

	/**
	 * Set heartbeat time.
	 * 
	 * @param time
	 */
	public synchronized void setHeartbeatTime(long time) {
		this.heartbeatTime = time;
	}

	/**
	 * Get heartbeat time.
	 * 
	 * @return
	 */
	public synchronized long getHearbeatTime() {
		return heartbeatTime;
	}

	/**
	 * Set storage load.
	 * 
	 * @param load
	 */
	public void setLoad(int load) {
		this.load = load;
	}

	/**
	 * Get storage load.
	 * 
	 * @return
	 */
	public int getLoad() {
		return load;
	}

	/**
	 * Get storage server id.
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * Get storage address.
	 * 
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Get all files that belongs to this storage server.
	 * 
	 * @return
	 */
	public synchronized List<File> getFiles() {
		return files;
	}

	/**
	 * Add a file to this storage server.
	 * 
	 * @param file
	 */
	public synchronized void addFile(File file) {
		files.add(file);
	}

	/**
	 * Remove a file from this storage server.
	 * 
	 * @param file
	 */
	public synchronized void removeFile(File file) {
		files.remove(file);
	}

	/**
	 * Get all files that needs to be migrated.
	 * 
	 * @return
	 */
	public synchronized Map<Storage, List<File>> getMigrateFiles() {
		Map<Storage, List<File>> result = migrateFiles;

		return result;
	}

	/**
	 * Add a file that needs to be migrated.
	 * 
	 * @param storage
	 *            Where this file can be find.
	 * @param file
	 */
	public synchronized void addMigrateFile(Storage storage, File file) {
		List<File> list = migrateFiles.get(storage);
		if (null == list) {
			list = new ArrayList<File>();
			migrateFiles.put(storage, list);
		}
		list.add(file);
	}

	/**
	 * Remove files that have already been modified.
	 * 
	 * @param files
	 *            {storage server address, {file id}}
	 */
	public synchronized void removeMigrateFiles(Map<String, List<String>> files) {
		List<String> list = null;
		for (Storage storage : migrateFiles.keySet()) {
			list = files.get(storage.getAddress());
			Iterator<File> iter = migrateFiles.get(storage).iterator();
			File file = null;
			while (iter.hasNext()) {
				file = iter.next();
				if (list.contains(file.getId())) {
					file.addLocation(this);
					addFile(file);
					iter.remove();
				}
			}
		}
	}
}
