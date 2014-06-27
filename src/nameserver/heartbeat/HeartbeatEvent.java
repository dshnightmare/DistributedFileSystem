package nameserver.heartbeat;

import nameserver.meta.Storage;

public class HeartbeatEvent
{
    private Storage storage;
    
    public HeartbeatEvent(Storage storage)
    {
        this.storage = storage;
    }
    
    public Storage getStorage()
    {
        return storage;
    }
}
