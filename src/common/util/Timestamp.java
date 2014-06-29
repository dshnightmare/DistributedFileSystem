package common.util;

public class Timestamp
{
    private static Timestamp instance = new Timestamp();

    private Timestamp()
    {
    }

    public static Timestamp getInstance()
    {
        return instance;
    }

    public synchronized Long getTimestamp()
    {
        return System.currentTimeMillis();
    }
}
