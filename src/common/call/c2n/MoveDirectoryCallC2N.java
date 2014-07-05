package common.call.c2n;

import common.call.Call;

public class MoveDirectoryCallC2N
    extends Call
{
    private static final long serialVersionUID = -3928327992229216635L;

    private String oldDirName;

    private String newDirName;

    public MoveDirectoryCallC2N(String oldDirName, String newDirName)
    {
        super(Call.Type.MOVE_DIRECTORY_C2N);
        this.oldDirName = oldDirName;
        this.newDirName = newDirName;
    }

    public String getOldDirectoryName()
    {
        return oldDirName;
    }

    public String getNewDirectoryName()
    {
        return newDirName;
    }
}
