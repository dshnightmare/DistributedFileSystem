package common.util;

public class Timestamp
{
    private static Timestamp instance = null;

    private Timestamp()
    {
    }

    public static Timestamp getInstance()
    {
        if (null == instance)
        {
            synchronized (Timestamp.class)
            {
                if (null == instance)
                {
                    instance = new Timestamp();
                }
            }
        }

        return instance;
    }

    public synchronized Long getTimestamp()
    {
        return System.currentTimeMillis();
    }
}
