package common.call.n2c;

import java.util.List;

import common.call.Call;

public class GetFileCallN2C
    extends Call
{
    private static final long serialVersionUID = -3738634987359667308L;

    private final List<String> locations;

    private String fid;

    public GetFileCallN2C(String fid, List<String> locations)
    {
        super(Call.Type.GET_FILE_N2C);
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
