package common.call.n2c;

import java.util.List;

import common.call.Call;
import common.call.Call.Type;

public class AppendFileCallN2C
    extends Call
{
    private static final long serialVersionUID = -3738634987359667308L;

    private final List<String> locations;

    private String fid;

    public AppendFileCallN2C(String fid, List<String> locations)
    {
        super(Call.Type.APPEND_FILE_N2C);
        this.fid = fid;
        this.locations = locations;
    }

    public List<String> getLocations()
    {
        return locations;
    }

    public String getFileId()
    {
        return fid;
    }
}
