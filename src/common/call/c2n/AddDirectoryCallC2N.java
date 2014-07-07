package common.call.c2n;

import common.call.Call;
/**
 * Call to add directory
 * <p>
 * @see Call
 * @author gengyufeng
 *
 */
public class AddDirectoryCallC2N
    extends Call
{
    private static final long serialVersionUID = -2817204910897703184L;

    private String dirName;

    public AddDirectoryCallC2N(String dirName)
    {
        super(Call.Type.ADD_DIRECTORY_C2N);
        this.dirName = dirName;
    }

    public String getDirName()
    {
        return dirName;
    }
}
