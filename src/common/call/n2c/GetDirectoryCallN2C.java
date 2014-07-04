package common.call.n2c;

import java.util.ArrayList;
import java.util.List;

import common.call.Call;

public class GetDirectoryCallN2C
    extends Call
{
    private static final long serialVersionUID = -2876278623815280597L;

    /**
     * Files and directories in the specified directory.
     */
    private List<String> filesAndDirectories = new ArrayList<String>();

    public GetDirectoryCallN2C(List<String> filesAndDirectories)
    {
        super(Call.Type.GET_DIRECTORY_N2C);
        this.filesAndDirectories = filesAndDirectories;
    }

    /**
     * Get files and directories in the specified directory.
     * 
     * @return
     */
    public List<String> getFilesAndDirectories()
    {
        return filesAndDirectories;
    }
}
