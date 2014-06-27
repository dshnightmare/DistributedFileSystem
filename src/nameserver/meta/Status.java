package nameserver.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.util.Timestamp;

public class Status
{
    private Map<Storage, Long> status = new HashMap<Storage, Long>();

    public synchronized void addStorage(Storage storage)
    {
        status.put(storage, Timestamp.getInstance().getTimestamp());
    }

    public synchronized List<Storage> allocateStorage(int count)
    {
        List<Storage> result = new ArrayList<Storage>();
        for (Storage s : status.keySet())
        {
            if (count <= 0)
                break;
            count--;
            result.add(s);
        }

        return result;
    }

    public synchronized void removeStorage(Storage storage)
    {
        status.remove(storage);
    }

    public synchronized boolean contains(String address)
    {
        for (Storage s : status.keySet())
        {
            if (0 == address.compareTo(s.getAddress()))
                return true;
        }
        return false;
    }

    public synchronized Storage getStorage(String address)
    {
        for (Storage s : status.keySet())
        {
            if (0 == address.compareTo(s.getAddress()))
                return s;
        }
        return null;
    }

    public synchronized List<Storage> getStorages()
    {
        List<Storage> result = new ArrayList<Storage>();
        for (Storage s : status.keySet())
        {
            result.add(s);
        }

        return result;
    }

    public synchronized void updateTimestamp(Storage storage)
    {
        status.put(storage, Timestamp.getInstance().getTimestamp());
    }
}
