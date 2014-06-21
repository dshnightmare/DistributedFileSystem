package nameserver.meta;

public class StorageStatus
{
    private long id;

    private boolean alive = true;
    
    public StorageStatus(long id)
    {
        this.id = id;
    }
    
    public long getId()
    {
        return id;
    }

    public synchronized boolean isAlive()
    {
        return alive;
    }

    public synchronized void setAlive(boolean alive)
    {
        this.alive = alive;
    }
}
