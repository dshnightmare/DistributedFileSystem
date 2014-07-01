package nameserver;

public class LogUtil
{
    private static LogUtil instance;
    
    private LogUtil()
    {
    }

    public synchronized static LogUtil getInstance()
    {
        if (null == instance)
            instance = new LogUtil();
        return instance;
    }
}
