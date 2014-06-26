package common.observe.call;

public class MoveFileCallC2N
    extends Call
{
    private static final long serialVersionUID = 1336355298379688981L;

    private String oldDirName;

    private String oldFileName;

    private String newDirName;

    private String newFileName;

    public MoveFileCallC2N(String oldDirName, String oldFileName,
        String newDirName, String newFileName)
    {
        super(Call.Type.MOVE_FILE_C2N);
        this.oldDirName = oldDirName;
        this.oldFileName = newFileName;
        this.newDirName = newDirName;
        this.newFileName = newFileName;
    }

    public String getOldDirName()
    {
        return oldDirName;
    }

    public String getOldFileName()
    {
        return oldFileName;
    }

    public String getNewDirName()
    {
        return newDirName;
    }

    public String getNewFileName()
    {
        return newFileName;
    }
}
