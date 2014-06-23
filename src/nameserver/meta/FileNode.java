package nameserver.meta;

import java.util.ArrayList;
import java.util.List;

public class FileNode
    extends Node
{
    private boolean isLocked = false;

    private long fid;

    private List<StorageStatus> locatoins = new ArrayList<StorageStatus>();

    public FileNode(String name, long fid)
    {
        super(name);
        this.fid = fid;
    }

    public synchronized boolean getLock()
    {
        if (isLocked)
            return false;
        else
        {
            isLocked = true;
            return true;
        }
    }

    public synchronized void releaseLock()
    {
        isLocked = false;
    }

    public synchronized long getFid()
    {
        return fid;
    }

    @Override
    public boolean isDirectory()
    {
        return false;
    }

    @Override
    public boolean isFile()
    {
        return false;
    }
}
