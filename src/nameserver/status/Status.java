package nameserver.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nameserver.status.StatusEvent.Type;

/**
 * Status of storage servers.
 * 
 * @author lishunyang
 * @see Storage
 * 
 */
public class Status implements StatusEventListener
{
    /**
     * Single pattern instance.
     */
    private static Status instance = new Status();

    /**
     * Storage status.
     */
    private List<Storage> status = new ArrayList<Storage>();

    private List<StatusEventListener> listeners =
        new ArrayList<StatusEventListener>();

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
        storage.addEventListener(this);
        status.add(storage);

        fireEvent(new StatusEvent(Type.STORAGE_REGISTERED, storage));
    }

    /**
     * Allocate specified number of storage to something.
     * 
     * @param count
     * @return
     */
    public synchronized List<Storage> allocateStorage(int count)
    {
        Collections.sort(status, new Comparator<Storage>(){   
            public int compare(Storage s1, Storage s2) {   
                return s1.getTaskSum() - s2.getTaskSum();   
             }   
         });   
        
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
        final boolean remove = status.remove(storage);

        System.out.println("@@@@@@@ " + remove);
        if (remove)
        {
            fireEvent(new StatusEvent(Type.STORAGE_DEAD, storage));
        }
    }

    /**
     * Test whether we have knowledge about specified storage server.
     * 
     * @param id
     * @return
     */
    public synchronized boolean contains(String id)
    {
        for (Storage s : status)
        {
            if (0 == id.compareTo(s.getId()))
                return true;
        }
        return false;
    }

    /**
     * Get storage server with specified address.
     * 
     * @param id
     * @return
     */
    public synchronized Storage getStorage(String id)
    {
        for (Storage s : status)
        {
            if (0 == id.compareTo(s.getId()))
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

    public synchronized int getStorageNum()
    {
        return status.size();
    }
    
    public void addEventListener(StatusEventListener listener)
    {
        listeners.add(listener);
    }

    public void removeEventListener(StatusEventListener listener)
    {
        listeners.remove(listener);
    }

    public void fireEvent(StatusEvent event)
    {
        for (StatusEventListener l : listeners)
            l.handle(event);
    }

    @Override
    public void handle(StatusEvent event)
    {
        fireEvent(event);
    }
}
