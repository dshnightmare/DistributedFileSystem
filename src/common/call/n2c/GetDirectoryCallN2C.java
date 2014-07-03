package common.call.n2c;

import java.util.ArrayList;
import java.util.List;

import common.call.Call;

public class GetDirectoryCallN2C
    extends Call
{
    private static final long serialVersionUID = -2876278623815280597L;

    /**
     * Files in the specified directory.
     */
    private List<String> fileList = new ArrayList<String>();

    /**
     * Directories in the specified directory.
     */
    private List<String> dirList = new ArrayList<String>();

    public GetDirectoryCallN2C(List<String> fileList, List<String> dirList)
    {
        super(Call.Type.GET_DIRECTORY_N2C);
        this.fileList = fileList;
        this.dirList = dirList;
    }

    public List<String> getFileList()
    {
        return fileList;
    }

    public List<String> getDirectoryList()
    {
        return dirList;
    }
}
