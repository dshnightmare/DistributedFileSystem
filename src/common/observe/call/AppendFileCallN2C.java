package common.observe.call;

import java.util.List;

public class AppendFileCallN2C
    extends Call
{
    private static final long serialVersionUID = -3738634987359667308L;

    private final List<String> locations;

    public AppendFileCallN2C(List<String> locations)
    {
        super(Call.Type.APPEND_FILE_N2C);
        this.locations = locations;
    }

    public List<String> getLocations()
    {
        return locations;
    }
}
