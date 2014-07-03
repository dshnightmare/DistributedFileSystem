package nameserver.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * File meta structure, holds file information such as file id, version, name,
 * read&write lock.
 * <p>
 * <strong>Warning:</strong> This structure is thread-unsafe.
 * 
 * @author lishunyang
 * 
 */
public class File {
	/**
	 * The separator is used to build full id.
	 * <p>
	 * bareId + SEPARATOR + version.
	 */
	private final static String SEPARATOR = "-";

	/**
	 * File name.
	 */
	private String name;

	/**
	 * Bare file id, this MUST be unique in the global.
	 */
	private final long bareId;

	/**
	 * File version, indicate which version this file's in.
	 */
	private long version = 0;

	/**
	 * Read and write lock.
	 */
	private ReadWriteLock rwLock = new ReentrantReadWriteLock();

	/**
	 * Indicate whether this file is valid. When a file has been successfully
	 * created, this should be set to true.
	 */
	private boolean valid = false;

	/**
	 * Where this file is being stored on.
	 */
	private List<Storage> locations = new ArrayList<Storage>();

	/**
	 * Construction method.
	 * 
	 * @param name
	 * @param bareId
	 */
	public File(String name, long bareId) {
		this.name = name;
		this.bareId = bareId;
	}

	/**
	 * Set file name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get file name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get file full id.
	 * 
	 * @return
	 */
	public String getId() {
		return bareId + SEPARATOR + version;
	}

	/**
	 * Set where this file is being stored at.
	 * 
	 * @param locations
	 */
	public void setLocations(List<Storage> locations) {
		this.locations = locations;
	}

	/**
	 * Add a location to storage this file.
	 * 
	 * @param storage
	 */
	public void addLocation(Storage storage) {
		if (!locations.contains(storage))
			this.locations.add(storage);
	}

	/**
	 * Get how many duplication of this file.
	 * 
	 * @return
	 */
	public int getLocationsCount() {
		return locations.size();
	}

	/**
	 * Remove one duplication from its storage list.
	 * 
	 * @param storage
	 */
	public void removeLocations(Storage storage) {
		this.locations.remove(storage);
	}

	/**
	 * Get locations where this file is being stored at.
	 * 
	 * @return
	 */
	public List<Storage> getLocations() {
		return locations;
	}

	/**
	 * Test whether this file is valid.
	 * 
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Set valid bit of this file.
	 * 
	 * @param valid
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * Update file version.
	 */
	public void updateVersion() {
		version++;
	}

	/**
	 * Get file version.
	 * 
	 * @return
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * Try to get read lock. It will for a specified time to get lock.
	 * 
	 * @param time
	 * @param unit
	 * @return True if get lock successfully, otherwise false.
	 */
	public boolean tryLockRead(long time, TimeUnit unit) {
		try {
			return rwLock.readLock().tryLock(time, unit);
		} catch (InterruptedException e) {
			e.printStackTrace();
			// Is that correct?
			rwLock.readLock().unlock();
			return false;
		}
	}

	/**
	 * Release read lock.
	 */
	public void unlockRead() {
		rwLock.readLock().unlock();
	}

	/**
	 * Try to get write lock. It will for a specified time to get lock.
	 * 
	 * @param time
	 * @param unit
	 * @return
	 */
	public boolean tryLockWrite(long time, TimeUnit unit) {
		try {
			return rwLock.writeLock().tryLock(time, unit);
		} catch (InterruptedException e) {
			e.printStackTrace();
			rwLock.writeLock().unlock();
			return false;
		}
	}

	/**
	 * Release write lock.
	 */
	public void unlockWrite() {
		rwLock.writeLock().unlock();
	}
}
