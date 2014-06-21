package nameserver.heartbeat;

import nameserver.meta.StorageNode;

public class HeartbeatEvent
{
    private StorageNode node;

    private Type type;

    public HeartbeatEvent(Type type, StorageNode node)
    {
        this.node = node;
        this.type = type;
    }

    public StorageNode getStorageNode()
    {
        return node;
    }

    public Type getType()
    {
        return type;
    }

    public enum Type
    {
        SYNC, DIED
    }
}
