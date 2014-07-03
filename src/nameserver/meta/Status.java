package nameserver.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * Status of storage servers.
 * 
 * @author lishunyang
 * @see Storage
 * 
 */
public class Status
{
    /**
     * Single pattern instance.
     */
    private static Status instance = new Status();

    /**
     * Storage status.
     */
    private List<Storage> status = new ArrayList<Storage>();

    /**
     * Construction method.
     */
    private Status()
    {
    }

    /**
     * Get single instance.
     * 
     * @return
     */
    public static Status getInstance()
    {
        return instance;
    }

    /**
     * Add new storage information. Allocate a new timestamp for it.
     * 
     * @param storage
     */
    public synchronized void addStorage(Storage storage)
    {
        status.add(storage);
    }

    /**
     * Allocate specified number of storage to something.
     * 
     * @param count
     * @return
     */
    public synchronized List<Storage> allocateStorage(int count)
    {
        List<Storage> result = new ArrayList<Storage>();
        for (Storage s : status)
        {
            if (count <= 0)
                break;
            count--;
            result.add(s);
        }

        return result;
    }

    /**
     * Remove specified storage information.
     * 
     * @param storage
     */
    public synchronized void removeStorage(Storage storage)
    {
        status.remove(storage);
    }

    /**
     * Test whether we have knowledge about specified storage server.
     * 
     * @param address
     * @return
     */
    public synchronized boolean contains(String address)
    {
        for (Storage s : status)
        {
            if (0 == address.compareTo(s.getAddress()))
                return true;
        }
        return false;
    }

    /**
     * Get storage server with specified address.
     * 
     * @param address
     * @return
     */
    public synchronized Storage getStorage(String address)
    {
        for (Storage s : status)
        {
            if (0 == address.compareTo(s.getAddress()))
                return s;
        }
        return null;
    }

    /**
     * Get all storage servers.
     * 
     * @return
     */
    public synchronized List<Storage> getStorages()
    {
        List<Storage> result = new ArrayList<Storage>();
        for (Storage s : status)
        {
            result.add(s);
        }

        return result;
    }
}
