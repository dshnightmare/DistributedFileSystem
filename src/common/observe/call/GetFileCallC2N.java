package common.observe.call;

public class GetFileCallC2N 
	extends Call{

	private static final long serialVersionUID = -6321724391912644392L;

	private String dirName;

    private String fileName;

    public GetFileCallC2N(long clientTaskId, String dirName, String fileName)
    {
        super(Call.Type.GET_FILE_C2N);
        this.dirName = dirName;
        this.fileName = fileName;
        setClientTaskId(clientTaskId);
    }

    public String getDirName()
    {
        return dirName;
    }

    public String getFileName()
    {
        return fileName;
    }
}
