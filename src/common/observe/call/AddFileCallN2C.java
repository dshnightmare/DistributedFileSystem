package common.observe.call;

import java.util.List;

public class AddFileCallN2C
    extends Call
{
    private static final long serialVersionUID = 32014432346467370L;

    private final List<String> locations;

    public AddFileCallN2C(List<String> locations)
    {
        super(Call.Type.ADD_FILE_N2C);
        this.locations = locations;
    }

    public List<String> getLocations()
    {
        return locations;
    }
}
