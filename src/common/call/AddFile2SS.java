package common.call;

import java.util.ArrayList;

import common.protocol.StorageNodeInfo;

public class AddFile2SS extends Call {
	private static final long serialVersionUID = -3216391836694114902L;
	private long fileID;
	private ArrayList<StorageNodeInfo> storageNodes;

	public AddFile2SS() {
		super(Call.Type.ADD_FILE_SS);
		storageNodes = new ArrayList<StorageNodeInfo>();
		// TODO Auto-generated constructor stub
	}

	public long getFileID() {
		return fileID;
	}

	public void setFileID(long fileID) {
		this.fileID = fileID;
	}

	public void addStorageNode(StorageNodeInfo storageNode) {
		if (storageNode != null)
			storageNodes.add(storageNode);
	}

	public ArrayList<StorageNodeInfo> getStorageNodes() {
		return storageNodes;
	}
}
