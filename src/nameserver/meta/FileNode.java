package nameserver.meta;

public class FileNode extends Node
{
    private boolean isLocked = false;

    private long fid;
    
    public FileNode(String name)
    {
        super(name);
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

    @Override
    public boolean isDirectory()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFile()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
