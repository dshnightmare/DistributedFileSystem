package common.call.c2n;

import common.call.Call;

public class RemoveFileCallC2N
    extends Call
{

    private static final long serialVersionUID = 7834245963090160026L;

    private String dirName;

    private String fileName;

    public RemoveFileCallC2N(String dirName, String fileName)
    {
        super(Call.Type.REMOVE_FILE_C2N);
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
