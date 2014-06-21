package storageserver;

public class StorageInfo {
	public int namespaceID;
	public long cTime;

	public StorageInfo() {
		this(0, 0L);
	}

	public StorageInfo(int namespaceID, long cTime) {
		this.namespaceID = namespaceID;
		this.cTime = cTime;
	}

	public int getNamespaceID() {
		return namespaceID;
	}

	public long getcTime() {
		return cTime;
	}
	
	public void setStorageInfo(StorageInfo from)
	{
		namespaceID = from.namespaceID;
		cTime = from.cTime;
	}
}
