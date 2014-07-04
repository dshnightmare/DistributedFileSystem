package common.call.c2n;

import common.call.Call;

public class GetDirectoryCallC2N
    extends Call
{
    private static final long serialVersionUID = -8496848383621955822L;

    private String dirName;

    public GetDirectoryCallC2N(String dirName)
    {
        super(Call.Type.GET_DIRECTORY_C2N);
        this.dirName = dirName;
    }

    public String getDirName()
    {
        return dirName;
    }
}
