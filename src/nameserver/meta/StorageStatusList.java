package nameserver.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageStatusList
{
    private List<StorageStatus> storages = new ArrayList<StorageStatus>();

    public synchronized void addNode(StorageStatus node)
    {
        storages.add(node);
        Collections.sort(storages);
    }

    public synchronized boolean contains(Long id)
    {
        for (StorageStatus s : storages)
        {
            if (s.getId() == id)
                return true;
        }
        return false;
    }

    public synchronized void removeNode(Long id)
    {
        storages.remove(id);
    }

    public synchronized int getSize()
    {
        return storages.size();
    }

    public synchronized void update()
    {
        Collections.sort(storages);
    }

    public synchronized List<StorageStatus> allocateStorages(int num)
    {
        List<StorageStatus> result = new ArrayList<StorageStatus>();
        for (StorageStatus s : storages)
        {
            if (num <= 0)
                break;
            result.add(s);
            num--;
        }
        return result;
    }
}
