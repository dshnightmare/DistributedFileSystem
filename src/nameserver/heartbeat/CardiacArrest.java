package nameserver.heartbeat;

import nameserver.meta.StorageStatus;

public class CardiacArrest
{
    private StorageStatus node;

    public CardiacArrest(StorageStatus node)
    {
        this.node = node;
    }

    public StorageStatus getStorageNode()
    {
        return node;
    }
}
