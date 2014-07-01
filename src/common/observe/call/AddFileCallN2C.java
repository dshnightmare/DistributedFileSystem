package common.observe.call;

import java.util.List;

public class AddFileCallN2C
    extends Call
{
    private static final long serialVersionUID = 32014432346467370L;
    
    private long fid;

    private final List<String> locations;

    public AddFileCallN2C(long fid, List<String> locations)
    {
        super(Call.Type.ADD_FILE_N2C);
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
