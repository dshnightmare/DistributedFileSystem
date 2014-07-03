package common.call.c2n;

import common.call.Call;

public class AddFileCallC2N
    extends Call
{
    private static final long serialVersionUID = -805598603013291328L;

    private String dirName;

    private String fileName;

    public AddFileCallC2N(String dirName, String fileName)
    {
        super(Call.Type.ADD_FILE_C2N);
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
