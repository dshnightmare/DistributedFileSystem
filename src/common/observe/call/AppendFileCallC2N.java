package common.observe.call;

public class AppendFileCallC2N
    extends Call
{
    private static final long serialVersionUID = 5944057489040298495L;

    private final String dirName;

    private final String fileName;

    public AppendFileCallC2N(String dirName, String fileName)
    {
        super(Call.Type.APPEND_FILE_C2N);
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
