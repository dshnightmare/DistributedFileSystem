package nameserver.meta;

import java.util.HashMap;
import java.util.Map;

public class StorageStatusList
{
    private Map<Long, StorageStatus> storages = new HashMap<Long, StorageStatus>();
    
    public synchronized void addNode(StorageStatus node)
    {
        storages.put(node.getId(), node);
    }
    
    public synchronized boolean contains(Long id)
    {
        return storages.containsKey(id);
    }
    
    public synchronized void removeNode(Long id)
    {
        storages.remove(id);
    }
}
