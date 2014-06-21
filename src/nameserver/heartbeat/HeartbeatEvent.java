package nameserver.heartbeat;

import nameserver.meta.StorageStatus;

public class HeartbeatEvent
{
    private StorageStatus node;

    private Type type;

    public HeartbeatEvent(Type type, StorageStatus node)
    {
        this.node = node;
        this.type = type;
    }

    public StorageStatus getStorageNode()
    {
        return node;
    }

    public Type getType()
    {
        return type;
    }

    public enum Type
    {
        SYNC("SYNC"), DIED("DIED");
        
        private String name;
        private Type(String name)
        {
            this.name = name;
        }
        
        @Override
        public String toString()
        {
            return name;
        }
    }
}
