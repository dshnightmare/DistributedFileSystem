package common.observe.call;

import java.util.List;

public class AppendFileCallN2C
    extends Call
{
    private static final long serialVersionUID = -3738634987359667308L;

    private final List<String> locations;

    private long fid;

    public AppendFileCallN2C(long fid, List<String> locations)
    {
        super(Call.Type.APPEND_FILE_N2C);
        this.fid = fid;
        this.locations = locations;
    }

    public List<String> getLocations()
    {
        return locations;
    }

    public long getFileId()
    {
        return fid;
    }
}
