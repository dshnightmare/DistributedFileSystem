package nameserver;

public class FileIdUtil
{
    private static FileIdUtil instance = null;

    private static Long idSeed = null;

    private FileIdUtil()
    {
    }

    public synchronized static FileIdUtil getInstance()
    {
        if (null == instance)
            instance = new FileIdUtil();

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
