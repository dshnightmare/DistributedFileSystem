package common.call;

public class GetFileCallC2N 
	extends Call{

	private static final long serialVersionUID = -6321724391912644392L;

	private String dirName;

    private String fileName;

    public GetFileCallC2N(String dirName, String fileName)
    {
        super(Call.Type.GET_FILE_C2N);
        this.dirName = dirName;
        this.fileName = fileName;
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
