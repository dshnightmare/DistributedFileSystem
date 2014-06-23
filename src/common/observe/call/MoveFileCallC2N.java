package common.observe.call;

public class MoveFileCallC2N
    extends Call
{
    private static final long serialVersionUID = 1336355298379688981L;

    private String oldPath;

    private String newPath;

    public MoveFileCallC2N(String oldPath, String newPath)
    {
        super(Call.Type.MOVE_FILE_C2N);
        this.oldPath = oldPath;
        this.newPath = newPath;
    }

    public String getOldPath()
    {
        return oldPath;
    }

    public String getNewPath()
    {
        return newPath;
    }
}
