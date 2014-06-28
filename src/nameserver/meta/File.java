package nameserver.meta;

import java.util.ArrayList;
import java.util.List;

public class File
{
    private String name;

    private final long id;

    /**
     * Indicate whether this file has committed. If it's false, someone could be
     * using the file now.
     */
    private boolean valid = false;

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

    public synchronized boolean isValid()
    {
        return valid;
    }

    public synchronized void setValid(boolean valid)
    {
        this.valid = valid;
    }
}
