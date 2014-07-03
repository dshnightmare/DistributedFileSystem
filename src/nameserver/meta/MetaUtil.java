package nameserver.meta;

public class MetaUtil
{
    private static MetaUtil instance = null;

    private static Long idSeed = null;

    private MetaUtil()
    {
    }

    public synchronized static MetaUtil getInstance()
    {
        if (null == instance)
            instance = new MetaUtil();

        return instance;
    }

    public long getNextFileId()
    {
        synchronized (idSeed)
        {
            if (null == idSeed)
                return -1;

            idSeed++;
            return idSeed;
        }
    }
    
    public void setIdSeed(long seed)
    {
        idSeed = seed;
    }
}
