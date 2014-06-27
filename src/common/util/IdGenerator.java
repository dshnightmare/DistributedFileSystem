package common.util;

import java.util.Random;

public class IdGenerator
{
    private Random rand = new Random(System.currentTimeMillis());

    private static IdGenerator instance = null;

    private IdGenerator()
    {
    }

    public static IdGenerator getInstance()
    {
        if (null == instance)
        {
            synchronized (IdGenerator.class)
            {
                if (null == instance)
                {
                    instance = new IdGenerator();
                }
            }
        }

        return instance;
    }

    public synchronized Long getLongId()
    {
        return Math.abs(rand.nextLong());
    }

    public synchronized Integer getIntegerId()
    {
        return Math.abs(rand.nextInt());
    }
}
