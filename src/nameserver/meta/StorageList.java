package nameserver.meta;

import java.util.HashMap;
import java.util.Map;

public class StorageList
{
    private Map<Long, StorageNode> storages = new HashMap<Long, StorageNode>();
    
    public synchronized void addNode(StorageNode node)
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
