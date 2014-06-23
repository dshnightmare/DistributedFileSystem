package nameserver.meta;

import java.util.HashSet;
import java.util.Set;

public class StorageStatus
{
    private long id;

    private String address;

    private boolean alive = true;
    
    private Set<FileNode> files = new HashSet<FileNode>();

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
    
    public synchronized void addFile(FileNode node)
    {
        files.add(node);
    }
    
    public synchronized void removeFile(FileNode node)
    {
        files.remove(node);
    }
    
    public synchronized void diff(Set<Long> fidSet)
    {
        for (FileNode node : files)
        {
            if (fidSet.contains(node.getFid()))
            {
                fidSet.remove(node.getFid());
            }
        }
    }
}
