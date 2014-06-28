package nameserver.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Meta
{
    private static Meta instance = null;

    private Map<String, Directory> directories =
        new HashMap<String, Directory>();

    public static final String SEPERATOR = "/";
    
    private Lock lock = new ReentrantLock();

    private Meta()
    {
    }

    public static Meta getInstance()
    {
        if (null == instance)
        {
            synchronized (Meta.class)
            {
                if (null == instance)
                {
                    instance = new Meta();
                }
            }
        }

        return instance;
    }

    public synchronized Directory getDirectory(String dirName)
    {
        return directories.get(dirName);
    }

    public synchronized void addDirectory(Directory directory)
    {
        directories.put(directory.getName(), directory);
    }
    
    public synchronized void removeDirectory(String dirName)
    {
        directories.remove(dirName);
    }

    public synchronized boolean contains(String dirName)
    {
        return directories.containsKey(dirName);
    }
    
    public void lock(String dirName)
    {
        lock.lock();
    }
    
    public void unlock()
    {
        lock.unlock();
    }
    
    public boolean tryLock()
    {
        return lock.tryLock();
    }
}
