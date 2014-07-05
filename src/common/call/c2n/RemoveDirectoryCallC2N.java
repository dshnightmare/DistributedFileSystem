package common.call.c2n;

import common.call.Call;

public class RemoveDirectoryCallC2N
    extends Call
{
    private static final long serialVersionUID = -7582143207284111922L;

    private String dirName;

    public RemoveDirectoryCallC2N(String dirName)
    {
        super(Call.Type.REMOVE_DIRECTORY_C2N);
        this.dirName = dirName;
    }

    public String getDirectoryName()
    {
        return dirName;
    }

}
