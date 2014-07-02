package nameserver.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class File
{
    private String name;

    private final long id;

    private long version = 0;

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

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
        this.version = 0;
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

    public void setLocations(List<Storage> locations)
    {
        this.locations = locations;
    }

    public void addLocation(Storage storage)
    {
        if (!locations.contains(storage))
            this.locations.add(storage);
    }

    public int getLocationsCount()
    {
        return locations.size();
    }

    public void removeLocations(Storage storage)
    {
        this.locations.remove(storage);
    }

    public List<Storage> getLocations()
    {
        return locations;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public void updateVersion()
    {
        this.version++;
    }

    public long getVersion()
    {
        return version;
    }

    public boolean tryLockRead(long time, TimeUnit unit)
    {
        try
        {
            return rwLock.readLock().tryLock(time, unit);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            // Is that correct?
            rwLock.readLock().unlock();
            return false;
        }
    }

    public void unlockRead()
    {
        rwLock.readLock().unlock();
    }

    public boolean tryLockWrite(long time, TimeUnit unit)
    {
        try
        {
            return rwLock.writeLock().tryLock(time, unit);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            rwLock.writeLock().unlock();
            return false;
        }
    }

    public void unlockWrite()
    {
        rwLock.writeLock().unlock();
    }
}
