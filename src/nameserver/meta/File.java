package nameserver.meta;

import java.util.ArrayList;
import java.util.List;

public class File
{
    private String name;

    private final long id;

    private List<Storage> locations = new ArrayList<Storage>();

    public File(String name, long id)
    {
        this.name = name;
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public long getId()
    {
        return id;
    }

    public synchronized void setLocations(List<Storage> locations)
    {
        this.locations = locations;
    }

    public synchronized void addLocation(Storage storage)
    {
        this.locations.add(storage);
    }

    public synchronized void removeLocations(Storage storage)
    {
        this.locations.remove(storage);
    }

    public synchronized List<Storage> getLocations()
    {
        return locations;
    }
}
