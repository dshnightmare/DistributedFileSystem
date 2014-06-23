package nameserver.meta;

public class StorageStatus
{
    private long id;

    private String address;

    private boolean alive = true;

    public StorageStatus(long id, String address)
    {
        this.id = id;
        this.address = address;
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

    public synchronized String getAddress()
    {
        return address;
    }
}
