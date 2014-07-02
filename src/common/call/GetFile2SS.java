package common.call;

public class GetFile2SS extends Call {
	private static final long serialVersionUID = -5193287859291676301L;
	private long fileID;

	public long getFileID() {
		return fileID;
	}

	public void setFileID(long fileID) {
		this.fileID = fileID;
	}

	public GetFile2SS() {
		super(Call.Type.GET_FILE_SS);
		// TODO Auto-generated constructor stub
	}

}
