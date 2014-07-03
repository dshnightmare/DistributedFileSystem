package common.call.n2c;

import java.util.List;

import common.call.Call;

public class AddFileCallN2C
    extends Call
{
    private static final long serialVersionUID = 32014432346467370L;
    
    private String fid;

    private final List<String> locations;

    public AddFileCallN2C(String fid, List<String> locations)
    {
        super(Call.Type.ADD_FILE_N2C);
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
